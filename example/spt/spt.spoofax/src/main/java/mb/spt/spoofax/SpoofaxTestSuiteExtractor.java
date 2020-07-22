package mb.spt.spoofax;

import mb.common.region.Region;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.spt.ITestCase;
import mb.spt.ITestCaseBuilder;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationBuilder;
import mb.spt.ITestFixture;
import mb.spt.ITestFixtureBuilder;
import mb.spt.ITestFragment;
import mb.spt.ITestFragmentBuilder;
import mb.spt.ITestSuite;
import mb.spt.expectations.ITestExpectationExtractor;
import mb.spt.expectations.UnknownTestExpectation;
import mb.spt.expectations.spoofax.ISpoofaxTestExpectationExtractor;
import mb.spt.extract.ITestSuiteExtractor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;
import org.spoofax.terms.util.TermUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extracts a Spoofax {@link ITestSuite} class.
 */
public final class SpoofaxTestSuiteExtractor implements ITestSuiteExtractor<IStrategoTerm> {

    // Constructor names
    private static final String TESTSUITE_CONS = "TestSuite";
    private static final String NAME_CONS = "Name";
    private static final String START_SYMBOL_CONS = "StartSymbol";
    private static final String LANG_CONS = "Language";
    private static final String FIXTURE_CONS = "Fixture";
    private static final String TEST_CONS = "Test";
    private static final String SELECTION_CONS = "Selection";
    private static final String FRAGMENT_CONS = "Fragment";
    private static final String TAILPART_DONE_CONS = "Done";
    private static final String TAILPART_MORE_CONS = "More";

    // TODO: Inject a new instance in every class that requires it
    private final ISpoofaxTestSuiteBuilder testSuiteBuilder;
    private final ITestCaseBuilder testCaseBuilder;
    private final ITestFragmentBuilder testFragmentBuilder;
    private final ITestFixtureBuilder testFixtureBuilder;

    private final Logger log;
    private final ITermTracer termTracer;
    private final Set<ITestExpectationExtractor<IStrategoTerm>> expectationExtractors;

    /**
     * Initializes a new instance of the {@link SpoofaxTestSuiteExtractor} class.
     */
    @Inject public SpoofaxTestSuiteExtractor(
        // TODO: Ensure these can be injected
        LoggerFactory loggerFactory,
        ITermTracer termTracer,
        Set<ITestExpectationExtractor<IStrategoTerm>> expectationExtractors,
        ISpoofaxTestSuiteBuilder testSuiteBuilder,
        ITestCaseBuilder testCaseBuilder,
        ITestFragmentBuilder testFragmentBuilder,
        ITestFixtureBuilder testFixtureBuilder
    ) {
        this.log = loggerFactory.create(getClass());
        this.termTracer = termTracer;
        this.expectationExtractors = expectationExtractors;

        this.testSuiteBuilder = testSuiteBuilder;
        this.testCaseBuilder = testCaseBuilder;
        this.testFragmentBuilder = testFragmentBuilder;
        this.testFixtureBuilder = testFixtureBuilder;
    }

    /**
     * Extracts a test suite from the given test suite term.
     *
     * @param testSuiteTerm the desugared test suite term
     * @return the extracted test suite
     */
    public ITestSuite extract(IStrategoTerm desugaredTerm) {
        // TestSuite(headers, fixture?, decls)
        assertIsAppl(desugaredTerm, TESTSUITE_CONS, 3);
        return extractTestSuite((IStrategoAppl)desugaredTerm);
    }

    /**
     * Extracts a test suite from the given test suite term.
     *
     * @param testSuiteTerm the test suite term
     * @return the extracted test suite
     */
    private ITestSuite extractTestSuite(IStrategoAppl testSuiteTerm) {
        // TestSuite(headers, fixture?, decls)
        ISpoofaxTestSuiteBuilder builder = testSuiteBuilder.reset();

        // The fixture that occurs after all tests.
        final @Nullable ITestFixture[] finalFixture = { null };

        new TermVisitor() {
            private IStrategoAppl fixtureTerm = null;

            @Override public void preVisit(IStrategoTerm term) {
                if (!TermUtils.isAppl(term)) return;
                IStrategoAppl applTerm = (IStrategoAppl)term;
                switch (applTerm.getName()) {
                    case START_SYMBOL_CONS:
                        // StartSymbol(name)
                        assertIsAppl(applTerm, START_SYMBOL_CONS, 1);
                        log.debug("Using start symbol: {}", applTerm.getSubterm(0));
                        builder.withStartSymbol(TermUtils.toJavaStringAt(applTerm, 0));
                        break;
                    case LANG_CONS:
                        // Language(name)
                        assertIsAppl(applTerm, LANG_CONS, 1);
                        log.debug("Using language under test: {}", applTerm.getSubterm(0));
                        builder.withLanguageName(TermUtils.toJavaStringAt(applTerm, 0));
                        break;
                    case NAME_CONS:
                        // Name(name)
                        assertIsAppl(applTerm, NAME_CONS, 1);
                        log.debug("Using test suite name: {}", applTerm.getSubterm(0));
                        builder.withName(TermUtils.toJavaStringAt(applTerm, 0));
                        break;
                    case FIXTURE_CONS:
                        assertIsAppl(applTerm, FIXTURE_CONS, 6);
                        log.debug("Using test fixture: {}", fixtureTerm);
                        final ITestFixture initialFixture = extractInitialTestFixture(applTerm);
                        // Add the initial fixture.
                        builder.addTestElement(initialFixture);
                        // Delay adding the final fixture until we're done with all the test cases
                        finalFixture[0] = extractFinalTestFixture(applTerm);
                        break;
                    case TEST_CONS:
                        assertIsAppl(applTerm, TEST_CONS, 5);
                        ITestCase testCase = extractTestCase(applTerm);
                        builder.addTestElement(testCase);
                        break;
                }
            }
        }.visit(testSuiteTerm);

        if (finalFixture[0] != null) {
            // Add the final fixture, if any.
            builder.addTestElement(finalFixture[0]);
        }

        return builder.build();
    }

    /**
     * Extracts a test case from the given test case term.
     *
     * @param testCaseTerm the test case term
     * @return the extracted test case
     */
    private ITestCase extractTestCase(IStrategoAppl testCaseTerm) {
        // Test(description, openMarker, fragment, closeMarker, expectations)
        final ITestCaseBuilder builder = testCaseBuilder.reset();

        final IStrategoString descriptionTerm = TermUtils.toStringAt(testCaseTerm, 0);
        builder.withDescription(TermUtils.toJavaString(descriptionTerm));
        builder.withDescriptionRegion(termTracer.trace(descriptionTerm));

        builder.withFragment(extractTestFragment(TermUtils.toApplAt(testCaseTerm, 2)));

        final List<IStrategoTerm> expectationTerms = Arrays.asList(TermUtils.toListAt(testCaseTerm, 4).getAllSubterms());
        for (IStrategoTerm expectationTerm : expectationTerms) {
            builder.addTestExpectation(extractTestExpectation(expectationTerm));
        }

        return builder.build();
    }

    /**
     * Extracts a test fragment from the given test fragment term.
     *
     * @param testFragmentTerm the test fragment term
     * @return the extracted test fragment
     */
    private ITestFragment extractTestFragment(IStrategoAppl testFragmentTerm) {
        // Fragment(StringPart, TailPart)
        final ITestFragmentBuilder builder = testFragmentBuilder.reset();

        builder.withRegion(termTracer.trace(testFragmentTerm));

        // NOTE: Initial fixture is an element of the test suite.

        // Get the text and selections
        final List<Region> selections = new ArrayList<>();
        new TermVisitor() {
            // Because we visit left to right, top to bottom,
            // we can collect all selections and fragment text during this visit.
            @Override public void preVisit(IStrategoTerm term) {
                if(!TermUtils.isAppl(term)) return;

                IStrategoAppl applTerm = (IStrategoAppl)term;

                switch(applTerm.getName()) {
                    // collect the selected regions
                    case SELECTION_CONS: {
                        // Selection(openMarker, text, closeMarker)
                        assertIsAppl(applTerm, SELECTION_CONS, 3);
                        final @Nullable Region location = termTracer.trace(term.getSubterm(1));
                        builder.addSelection(location);
                        break;
                    }
                    case FRAGMENT_CONS: {
                        // Fragment(text, TailPart)
                        assertIsAppl(applTerm, FRAGMENT_CONS, 2);
                        IStrategoTerm textTerm = term.getSubterm(0);
                        final @Nullable Region location = termTracer.trace(textTerm);
                        builder.addPiece(location, TermUtils.toJavaString(textTerm));
                        break;
                    }
                    case TAILPART_MORE_CONS: {
                        // More(Selection(openMarker, text, closeMarker), text, Tailpart)
                        assertIsAppl(applTerm, TAILPART_MORE_CONS, 3);
                        assertIsApplAt(applTerm, 0, SELECTION_CONS, 3);
                        IStrategoTerm selectionTextTerm = term.getSubterm(0).getSubterm(1);
                        final @Nullable Region location = termTracer.trace(selectionTextTerm);
                        builder.addPiece(location, TermUtils.toJavaString(selectionTextTerm));

                        IStrategoTerm moreTextTerm = term.getSubterm(1);
                        final @Nullable Region location2 = termTracer.trace(moreTextTerm);
                        builder.addPiece(location2, TermUtils.toJavaString(moreTextTerm));
                        break;
                    }
                    case TAILPART_DONE_CONS:
                        // Done()
                        assertIsAppl(applTerm, TAILPART_DONE_CONS, 0);
                        // Nothing to do.
                        break;
                    default:
                        throw new IllegalArgumentException("Unexpected constructor " + applTerm.getName() + ": " + applTerm);
                }
            }
        }.visit(testFragmentTerm);

        // NOTE: Final fixture is an element of the test suite.

        return builder.build();
    }

    /**
     * Extracts a test expectation from the given test expectation term.
     *
     * @param testExpectationTerm the test expectation term
     * @return the extracted test expectation
     */
    private ITestExpectation extractTestExpectation(IStrategoTerm testExpectationTerm) {
        ITestExpectationExtractor<? super IStrategoTerm> extractor = getExtractor(testExpectationTerm);

        final ITestExpectationBuilder builder = extractor.extract(testExpectationTerm);
        builder.withRegion(termTracer.trace(testExpectationTerm));

        return builder.build();
    }

    /**
     * Finds an extractor that can handle the given test expectation term.
     *
     * When no extractor can handle the given test expectation term,
     * this method returns an extractor that can handle all test expectation terms
     * and produces an {@link UnknownTestExpectation}.
     *
     * This method never returns {@code null}.
     *
     * Do NOT check {@link mb.spt.expectations.ITestExpectationExtractor#canExtract()},
     * but be assured that the returned expectation extractor can handle the given test expectation term
     * even if {@code canExtract()} would return {@code false}. This happens, for example,
     * when this method returns the extractor for {@link UnknownTestExpectation}.
     *
     * @param testExpectationTerm the test expectation term
     * @return the extractor that can handle it
     */
    private ITestExpectationExtractor<? super IStrategoTerm> getExtractor(IStrategoTerm testExpectationTerm) {
        final List<ITestExpectationExtractor<? super IStrategoTerm>> candidates = new ArrayList<>(1);
        for(ITestExpectationExtractor<? super IStrategoTerm> extractor : expectationExtractors) {
            if(extractor.canExtract(testExpectationTerm)) {
                candidates.add(extractor);
            }
        }

        if (candidates.size() == 0) {
            // No extractor could handle the test expectation
            return new UnknownTestExpectation.Extractor(Collections.emptyList());
        } else if (candidates.size() == 1) {
            // One extractor could handle the test expectation
            return candidates.get(0);
        } else {
            // Multiple extractors could handle the test expectation
            return new UnknownTestExpectation.Extractor(
                // We set the names of the candidate extractors so they can be shown in the expectation error later
                candidates.stream().map(c -> c.getClass().getSimpleName()).collect(Collectors.toList())
            );
        }
    }

    /**
     * Extracts the test fixture at the start of the test suite.
     *
     * @param testFixtureTerm the test fixture term
     * @return the test fixture at the start of the test suite
     */
    private ITestFixture extractInitialTestFixture(IStrategoAppl testFixtureTerm) {
        // Fixture(openMarker, text1, openMarker, closeMarker, text2, closeMarker)
        final ITestFixtureBuilder builder = testFixtureBuilder.reset();

        final IStrategoTerm text1Term = testFixtureTerm.getSubterm(1);
        @Nullable final Region location = termTracer.trace(text1Term);
        final ITestFragment fragment = testFragmentBuilder.reset()
            .addPiece(location, TermUtils.toJavaString(text1Term))
            .build();
        builder.withFragment(fragment);

        return builder.build();
    }

    /**
     * Extracts the test fixture at the end of the test suite.
     *
     * @param testFixtureTerm the test fixture term
     * @return the test fixture at the end of the test suite
     */
    private ITestFixture extractFinalTestFixture(IStrategoAppl testFixtureTerm) {
        // Fixture(openMarker, text1, openMarker, closeMarker, text2, closeMarker)
        final ITestFixtureBuilder builder = testFixtureBuilder.reset();

        final IStrategoTerm text2Term = testFixtureTerm.getSubterm(4);
        @Nullable final Region location = termTracer.trace(text2Term);
        final ITestFragment fragment = testFragmentBuilder.reset()
            .addPiece(location, TermUtils.toJavaString(text2Term))
            .build();
        builder.withFragment(fragment);

        return builder.build();
    }

    /**
     * Asserts that the term is a constructor application with the specified name and arity.
     *
     * @param term the term to check
     * @param consName the constructor name
     * @param arity the arity
     */
    // TODO: Move to term library
    private static void assertIsAppl(IStrategoTerm term, String consName, int arity) {
        if (!TermUtils.isAppl(term, consName, arity)) {
            throw new IllegalArgumentException("Expected " + consName + "`" + arity +
                ", got " + getTermUserDescription(term) + ": " + term);
        }
    }

    /**
     * Asserts that the specified subterm is a constructor application with the specified name and arity.
     *
     * @param term the term whose subterm to check
     * @param index the zero-based index of the subterm
     * @param consName the constructor name
     * @param arity the arity
     */
    // TODO: Move to term library
    private static void assertIsApplAt(IStrategoTerm term, int index, String consName, int arity) {
        if (!TermUtils.isApplAt(term, index, consName, arity)) {
            final IStrategoTerm subterm = term.getSubterm(index);
            throw new IllegalArgumentException("Expected " + consName + "`" + arity +
                ", got " + getTermUserDescription(subterm) + ": " + subterm);
        }
    }

    /**
     * Gets a human-readable description of a term, used in exception messages.
     *
     * @param term the term
     * @return the user description
     */
    // TODO: Move to term library
    private static String getTermUserDescription(IStrategoTerm term) {
        switch (term.getType()) {
            // @formatter:off
            case APPL:          return ((IStrategoAppl)term).getName() + "`" + term.getSubtermCount();
            case LIST:          return "list";
            case INT:           return "int";
            case REAL:          return "real";
            case STRING:        return "string";
            case CTOR:          return "ref";
            case TUPLE:         return "()`" + term.getSubtermCount();
            case REF:           return "ref";
            case BLOB:          return "blob<" + term.getClass().getSimpleName() + ">";
            case PLACEHOLDER:   return "placeholder";
            default:            return "unknown";
            // @formatter:on
        }
    }
}
