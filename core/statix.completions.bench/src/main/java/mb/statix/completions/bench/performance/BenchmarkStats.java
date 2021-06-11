package mb.statix.completions.bench.performance;

import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.statix.completions.TermCompleter;
import mb.statix.completions.bench.completeness.CompletenessTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Gathers the statistics for the benchmark.
 */
public final class BenchmarkStats {

    private static final SLF4JLoggerFactory loggerFactory = new SLF4JLoggerFactory();
    private static final Logger log = loggerFactory.create(CompletenessTest.class);

    // Test metadata
    private String testName = null;
    private int testIndex = -1;
    private TestGenerator.TestSuiteMetadata testSuiteMetadata = null;
    private TestGenerator.TestCaseMetadata testCaseMetadata = null;

    // Absolute time of each step
    private long prepareStartTime = -1;
    private long analyzeStartTime = -1;
    private long completeStartTime = -1;
    private long completeEndTime = -1;

    // Other measurements
    private int proposalCount = -1;

    // Cumulative time spent in each phase
    private long expandingPredicatesTime = 0;
    private long expandingInjectionsTime = 0;
    private long expandingQueriesTime = 0;
    private long expandingDeterministicTime = 0;

    /**
     * Starts a test.
     */
    public void startTest(String testName, int testIndex, TestGenerator.TestSuiteMetadata testSuiteMetadata, TestGenerator.TestCaseMetadata testCaseMetadata) {
        this.testName = testName;
        this.testIndex = testIndex;
        this.testSuiteMetadata = testSuiteMetadata;
        this.testCaseMetadata = testCaseMetadata;
        this.prepareStartTime = System.nanoTime();
    }

    /**
     * Starts analysis of the test term.
     */
    public void startAnalysis() {
        this.analyzeStartTime = System.nanoTime();
    }

    /**
     * Starts code completion algorithm.
     */
    public void startCompletion() {
        this.completeStartTime = System.nanoTime();
    }

    /**
     * Adds time spent on expanding predicates.
     *
     * @param time the time, in nanoseconds
     */
    public void addExpandPredicateTime(long time) {
        this.expandingPredicatesTime += time;
    }

    /**
     * Adds time spent on expanding injections.
     *
     * @param time the time, in nanoseconds
     */
    public void addExpandInjectionTime(long time) {
        this.expandingInjectionsTime += time;
    }

    /**
     * Adds time spent on expanding queries.
     *
     * @param time the time, in nanoseconds
     */
    public void addExpandQueriesTime(long time) {
        this.expandingQueriesTime += time;
    }

    /**
     * Adds time spent on expanding deterministically.
     *
     * @param time the time, in nanoseconds
     */
    public void addExpandDeterministicTime(long time) {
        this.expandingDeterministicTime += time;
    }

    /**
     * Finishes code completion algorithm.
     *
     * @param proposals the proposals that where returned
     */
    public void finishCompletion(List<TermCompleter.CompletionSolverProposal> proposals) {
        this.completeEndTime = System.nanoTime();
        this.proposalCount = proposals.size();
    }

    /**
     * Finishes the test.
     */
    public void finishTest() {
        assert testName != null;
        assert testIndex >= 0;
        assert testSuiteMetadata != null;
        assert testCaseMetadata != null;

        assert prepareStartTime >= 0;
        assert analyzeStartTime >= 0;
        assert completeStartTime >= 0;
        assert completeEndTime >= 0;

        assert proposalCount >= 0;
    }

    /**
     * Prints a summary of the recorded information.
     */
    public void printSummary() {
        log.info("========== {} ({}) ==========\n" +
                "Proposals: {}\n" +
                "Sort: {}\n" +
                "Text size: {}\n" +
                "AST size: {}\n" +
                "\n" +
                "Completion time: {} ms\n" +
                "Analysis time: {} ms\n" +
                "Preparation time: {} ms\n" +
                "Time per proposal: {} ms/proposal\n" +
                "\n" +
                "Cumulative stage times:\n" +
                "- Expanding predicates: {} ms\n" +
                "- Expanding injections: {} ms\n" +
                "- Expanding queries: {} ms\n" +
                "- Expanding deterministically: {} ms",
            testName, testIndex,
            proposalCount,
            testCaseMetadata.getSort(),
            testCaseMetadata.getTextSize(),
            testCaseMetadata.getAstSize(),

            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(completeEndTime - completeStartTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(completeStartTime - analyzeStartTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(analyzeStartTime - prepareStartTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis((completeEndTime - completeStartTime) / proposalCount)),

            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingPredicatesTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingInjectionsTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingQueriesTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingDeterministicTime))
        );
    }

}
