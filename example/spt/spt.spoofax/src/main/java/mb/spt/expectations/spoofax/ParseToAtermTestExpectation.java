package mb.spt.expectations.spoofax;

import mb.common.message.Severity;
import mb.common.region.Region;
import mb.common.result.Result;
import mb.common.util.ListView;
import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.pie.api.Pie;
import mb.pie.api.Task;
import mb.spt.FragmentUtils;
import mb.spt.ISpoofaxTestCodeInput;
import mb.spt.ITestCase;
import mb.spt.ITestInput;
import mb.spt.ITestSuite;
import mb.spt.expectations.ITestExpectationBuilder;
import mb.spt.expectations.ITestExpectationEvaluator;
import mb.spt.expectations.ITestExpectationResult;
import mb.spt.expectations.ITestExpectationResultBuilder;
import mb.spt.expectations.TestExpectation;
import mb.spt.expectations.TestExpectationResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;
import mb.common.message.Message;

import javax.inject.Inject;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Expectation that tests whether the fragment parses to the expected ATerm.
 */
public final class ParseToAtermTestExpectation extends TestExpectation {

    private final IStrategoTerm expectedTerm;

    /**
     * Initializes a new instance of the {@link TestExpectation} class.
     *
     * @param region the region of the test expectation
     * @param expectedTerm the expected ATerm
     */
    protected ParseToAtermTestExpectation(@Nullable Region region, IStrategoTerm expectedTerm) {
        super(region);
        this.expectedTerm = expectedTerm;
    }

    /**
     * Gets the expected ATerm.
     *
     * @return the expected ATerm
     */
    public IStrategoTerm getExpectedTerm() {
        return expectedTerm;
    }



    /**
     * Builder for {@link ParseToAtermTestExpectation}.
     */
    public static final class Builder implements ITestExpectationBuilder {

        private @Nullable Region region = null;
        private @Nullable IStrategoTerm expectedTerm = null;

        @Override public Builder withRegion(@Nullable Region region) {
            this.region = region;
            return this;
        }

        public Builder withExpectedTerm(IStrategoTerm expectedTerm) {
            this.expectedTerm = expectedTerm;
            return this;
        }

        @Override public ParseToAtermTestExpectation build() {
            checkFieldNotNull("expectedTerm", expectedTerm);

            return new ParseToAtermTestExpectation(region, expectedTerm);
        }

        @Override public Builder reset() {
            this.region = null;
            this.expectedTerm = null;

            return this;
        }
    }



    /**
     * Extractor for {@link ParseToAtermTestExpectation}.
     */
    public static final class Extractor implements ISpoofaxTestExpectationExtractor {

        // ParseToAterm(ToAterm(ast))
        private static final String PARSETOATERM_CONS = "ParseToAterm";
        private static final String TOATERM_CONS = "ToAterm";

        @Inject public Extractor() {}

        @Override public boolean canExtract(IStrategoTerm expectationTerm) {
            return TermUtils.isAppl(expectationTerm, PARSETOATERM_CONS, 1)
                && TermUtils.isApplAt(expectationTerm, 0, TOATERM_CONS, 1);
        }

        @Override public Builder extract(IStrategoTerm expectationTerm) {
            final Builder builder = new Builder();

            builder.withExpectedTerm(expectationTerm.getSubterm(0).getSubterm(0));

            return builder;
        }
    }


    /**
     * Evaluator for {@link ParseToAtermTestExpectation}.
     */
    public static final class Evaluator implements ITestExpectationEvaluator<ParseToAtermTestExpectation> {

        private final ITestExpectationResultBuilder testExpectationResultBuilder;
        private final Pie pie;
        private final ISpoofaxTestCodeInputProvider testCodeInputProvider;

        @Inject public Evaluator(Pie pie, ITestExpectationResultBuilder testExpectationResultBuilder, ISpoofaxTestCodeInputProvider testCodeInputProvider) {
            this.pie = pie;
            this.testExpectationResultBuilder = testExpectationResultBuilder;
            this.testCodeInputProvider = testCodeInputProvider;
        }

        @Override public ITestExpectationResult evaluate(ParseToAtermTestExpectation testExpectation, ITestCase testCase, ITestSuite testSuite)
            throws ExecException, InterruptedException {
            final ITestExpectationResultBuilder builder = testExpectationResultBuilder.reset();
            builder.withTestExpectation(testExpectation);

            final ISpoofaxTestCodeInput input = testCodeInputProvider.get(testCase, testSuite);
            final String fragmentText = input.getText();
            final Task<Result<IStrategoTerm, ?>> task = null; // TODO: Get the parse task
            final Result<IStrategoTerm, ?> parseResult;
            try (MixedSession session = pie.newSession()) {
                parseResult = session.require(task);
            }

            parseResult.ifElse(actualTerm -> {
                final IStrategoTerm expectedTerm = testExpectation.getExpectedTerm();
                if (expectedTerm.match(actualTerm)) {
                    // FIXME: Match prob ably won't work, since the expected AST is in SPT ATerm terms, not literal ATerm
                    builder.isSuccessful(true);
                } else {
                    builder.isSuccessful(false);
                    // TODO: Make a nice diff
                    builder.addMessage(new Message(String.format(
                        "The fragment did not parse to the expected ATerm.\nParse result was: %1$s\nExpected result was: %2$s",
                        actualTerm, expectedTerm), Severity.Error, testExpectation.getRegion()));
                }
            }, err -> {
                builder.isSuccessful(false);
                builder.addMessage(new Message("Expected the input fragment to parse successfully: " + err.getMessage(), Severity.Error, testExpectation.getRegion()));
            });

            return builder.build();
        }
    }

}
