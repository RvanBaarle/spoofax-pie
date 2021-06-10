package mb.statix.completions.bench.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.resource.DefaultResourceKey;
import mb.resource.ResourceKey;
import mb.statix.common.PlaceholderVarMap;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StatixSpec;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.completions.TermCompleter;
import mb.statix.spec.Spec;
import mb.strategies.StrategyEventHandler;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.io.TAFTermReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * A test benchmark.
 */
public abstract class TestBenchmark {

    private final ITermFactory factory;
    private final StatixSpec spec;
    private final String specName;
    private final String rootRuleName;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private static final SLF4JLoggerFactory loggerFactory = new SLF4JLoggerFactory();
    private static final Logger log = loggerFactory.create(TestBenchmark.class);
    private final TermCompleter completer;

    public TestBenchmark(ITermFactory factory, StatixSpec spec, String specName, String rootRuleName, TermCompleter completer) {
        this.factory = factory;
        this.spec = spec;
        this.specName = specName;
        this.rootRuleName = rootRuleName;
        this.completer = completer;
        this.mapper.findAndRegisterModules();
    }

    /**
     * Runs all tests.
     *
     * @param inputDirectory the input directory where the test files reside
     */
    public abstract void testAll(Path inputDirectory) throws IOException, InterruptedException;

    /**
     * Runs all test cases in a test suite.
     *
     * @param inputDirectory the input directory where the test files reside
     * @param testName the filename of the test (e.g, {@code xmpl/my_test.tig})
     */
    public void runTestSuite(Path inputDirectory, String testName) throws IOException, InterruptedException {
        final TestGenerator.TestSuiteMetadata metadata = readMetadata(inputDirectory.resolve(testName + ".yml"), TestGenerator.TestSuiteMetadata.class, this.mapper);
        final int testCount = metadata.getTestCaseCount();

        for (int i = 0; i < testCount; i++) {
            runTestCase(inputDirectory, testName, i);
        }
    }

    /**
     * Runs a test case.
     *
     * @param inputDirectory the input directory where the test files reside
     * @param testName the filename of the test (e.g, {@code xmpl/my_test.tig})
     * @param testIndex the zero-based index of the test (e.g., {@code 0})
     */
    private void runTestCase(Path inputDirectory, String testName, int testIndex) throws IOException, InterruptedException {
        long prepareStartTime = System.nanoTime();
        final String testCaseName = String.format("%s.test_%04d", testName, testIndex);

        // Prepare the Spec
        final StatixAnalyzer analyzer = new StatixAnalyzer(this.spec, this.factory, loggerFactory);
        precomputeOrderIndependentRules(this.spec.getSpec());

        // Read the AST
        final IStrategoTerm incompleteStrategoTerm = readTerm(inputDirectory.resolve(testCaseName + ".aterm"), this.factory);
        final ITerm incompleteTerm = toIndexedNablTerm(incompleteStrategoTerm, this.factory);
        final TermAndPlaceholder termAndPlaceholder = replacePlaceholders(incompleteTerm);
        final ITerm replacedTerm = termAndPlaceholder.term;
        final ITermVar var = termAndPlaceholder.placeholder;

        try(final StrategyEventHandler eventHandler = StrategyEventHandler.none()) {// new DebugEventHandler(Paths.get("debug.yml"))) {
            // Analyze the AST
            long analyzeStartTime = System.nanoTime();
            SolverState startState = analyzer.createStartState(replacedTerm, this.specName, this.rootRuleName)
                .withExistentials(Collections.singletonList(var))
                .precomputeCriticalEdges(this.spec.getSpec());
            SolverContext ctx = analyzer.createContext(eventHandler);
            SolverState state = analyzer.analyze(ctx, startState);

            // Complete
            long completeStartTime = System.nanoTime();
            List<TermCompleter.CompletionSolverProposal> proposals = completer.complete(ctx, TestBenchmark::isInj, state, var);
            long completeEndTime = System.nanoTime();

            // Record:
            // - completion time
            final long completionTime = completeEndTime - completeStartTime;
            // - analysis time
            final long analysisTime = completeStartTime - analyzeStartTime;
            // - preparation time
            final long preparationTime = analyzeStartTime - prepareStartTime;
            // - proposals found
            final int proposalCount = proposals.size();
            // TODO: And copy:
            // - ast size
            // - text size
            // - sort
            // TODO: And write to CSV (or YAML)
            System.out.println("===== " + testCaseName + " =====");
            System.out.println("Proposal   count: " + proposalCount);
            System.out.println("Completion  time: " + completionTime / 1000000 + " ms");
            System.out.println("Analysis    time: " + analysisTime / 1000000 + " ms");
            System.out.println("Preparation time: " + preparationTime / 1000000 + " ms");
            System.out.println("Time/proposal   : " + (completionTime / proposalCount) / 1000000 + " ms");
        }
    }

    /**
     * Reads the term from the specified path.
     *
     * @param path the path to read from
     * @param factory the term factory to use
     * @return the read term
     */
    private static IStrategoTerm readTerm(Path path, ITermFactory factory) throws IOException {
        final TAFTermReader termReader = new TAFTermReader(factory);
        try (InputStream inputStream = Files.newInputStream(path)) {
            return termReader.parseFromStream(inputStream);
        }
    }

    /**
     * Reads the metadata from the specified path.
     *
     * @param path the path to read from
     * @param cls the class of the metadata to read
     * @param mapper the YAML mapper
     * @param <T> the type of metadata
     * @return the read metadata
     */
    private static <T> T readMetadata(Path path, Class<T> cls, ObjectMapper mapper) throws IOException {
        try(final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return mapper.readValue(reader, cls);
        }
    }

    /**
     * Converts a Stratego term into a NaBL term annotated with term indices.
     *
     * @param term the Stratego term
     * @param factory the term factory
     * @return the NaBL term with term indices
     */
    private static ITerm toIndexedNablTerm(IStrategoTerm term, ITermFactory factory) {
        final StrategoTerms strategoTerms = new StrategoTerms(factory);
        final ResourceKey resourceKey = new DefaultResourceKey("test", "ast");

        final IStrategoTerm annotatedTerm = StrategoTermIndices.index(term, resourceKey.toString(), factory);
        return strategoTerms.fromStratego(annotatedTerm);
    }

    /**
     * Replaces the placeholder in the term with a term variable.
     *
     * @param term the term
     * @return the tuple of the term with the placeholder replaced by a term variable, and the term variable
     */
    private static TermAndPlaceholder replacePlaceholders(ITerm term) {
        final ResourceKey resourceKey = new DefaultResourceKey("test", "ast");
        final PlaceholderVarMap placeholderVarMap = new PlaceholderVarMap(resourceKey.toString());
        ITerm replacedTerm = StrategoPlaceholders.replacePlaceholdersByVariables(term, placeholderVarMap);
        assert placeholderVarMap.getVars().size() == 1 : "Expected a single placeholder, found " + placeholderVarMap.getVars().size();
        return new TermAndPlaceholder(replacedTerm, placeholderVarMap.getVars().iterator().next());
    }

    /**
     * Precomputes the order-independent rules for the given spec.
     * @param spec the Statix spec
     */
    private static void precomputeOrderIndependentRules(Spec spec) {
        for(final String ruleName : spec.rules().getRuleNames()) {
            spec.rules().getOrderIndependentRules(ruleName);
        }
    }

    /**
     * Determines whether the term is an injection.
     *
     * @param term the term to test
     * @return {@code true} when it is an injection; otherwise, {@code false}
     */
    private static boolean isInj(ITerm term) {
        // We use a heuristic here.
        return term instanceof IApplTerm
            && ((IApplTerm)term).getArity() == 1
            && ((IApplTerm)term).getOp().contains("2");
    }

    /**
     * A term and placeholder tuple.
     */
    private static class TermAndPlaceholder {
        private final ITerm term;
        private final ITermVar placeholder;

        private TermAndPlaceholder(ITerm term, ITermVar placeholder) {
            this.term = term;
            this.placeholder = placeholder;
        }
    }

}
