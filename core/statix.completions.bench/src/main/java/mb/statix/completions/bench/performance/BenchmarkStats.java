package mb.statix.completions.bench.performance;

import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.statix.completions.TermCompleter;
import mb.statix.completions.bench.completeness.CompletenessTest;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
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
    private String testCaseName = null;
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
    public void startTest(String testName, int testIndex, String testCaseName, TestGenerator.TestSuiteMetadata testSuiteMetadata, TestGenerator.TestCaseMetadata testCaseMetadata) {
        this.testName = testName;
        this.testIndex = testIndex;
        this.testCaseName = testCaseName;
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
    public CsvRow finishTest() {
        assert testName != null;
        assert testIndex >= 0;
        assert testCaseName != null;
        assert testSuiteMetadata != null;
        assert testCaseMetadata != null;

        assert prepareStartTime >= 0;
        assert analyzeStartTime >= 0;
        assert completeStartTime >= 0;
        assert completeEndTime >= 0;

        assert proposalCount >= 0;

        final CsvRow row = new CsvRow();
        row.testName = testName;
        row.testIndex = testIndex;
        row.testCaseName = testCaseName;

        row.proposalCount = proposalCount;
        row.sort = testCaseMetadata.getSort();
        row.textSize = testCaseMetadata.getTextSize();
        row.astSize = testCaseMetadata.getAstSize();

        row.preparationTime = analyzeStartTime - prepareStartTime;
        row.analysisTime = completeStartTime - analyzeStartTime;
        row.completionTime = completeEndTime - completeStartTime;

        row.expandingPredicatesTime = expandingPredicatesTime;
        row.expandingInjectionsTime = expandingInjectionsTime;
        row.expandingQueriesTime = expandingQueriesTime;
        row.expandingDeterministicTime = expandingDeterministicTime;
        return row;
    }

    /**
     * A row in the CSV file.
     */
    public static class CsvRow {

        // NOTE: Use the same order for the headers
        public String testName;
        public int testIndex;
        public String testCaseName;

        public int proposalCount;
        public String sort;
        public int textSize;
        public int astSize;

        public long preparationTime;
        public long analysisTime;
        public long completionTime;

        public long expandingPredicatesTime;
        public long expandingInjectionsTime;
        public long expandingQueriesTime;
        public long expandingDeterministicTime;

        // NOTE: Use the same order for the printing
        private static final String[] csvHeaders = new String[] {
            "testName",
            "testIndex",
            "testCaseName",

            "proposalCount",
            "sort",
            "textSize",
            "astSize",

            "preparationTime",
            "analysisTime",
            "completionTime",

            "expandingPredicatesTime",
            "expandingInjectionsTime",
            "expandingQueriesTime",
            "expandingDeterministicTime",
        };

        /**
         * Prints the recorded information to a CSV.
         *
         * @param printer the CSV printer
         */
        public void printToCsv(CSVPrinter printer) throws IOException {
            // NOTE: Use the same order for the fields
            printer.printRecord(
                this.testName,
                this.testIndex,
                this.testCaseName,

                this.proposalCount,
                this.sort,
                this.textSize,
                this.astSize,

                this.preparationTime,
                this.analysisTime,
                this.completionTime,

                this.expandingPredicatesTime,
                this.expandingInjectionsTime,
                this.expandingPredicatesTime,
                this.expandingDeterministicTime
            );
        }

        /**
         * Gets the CSV header names.
         *
         * @return the CSV header names
         */
        public static String[] getCsvHeaders() {
            return csvHeaders;
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
                    "Times:\n" +
                    "- Completion time: {} ms\n" +
                    "- Analysis time: {} ms\n" +
                    "- Preparation time: {} ms\n" +
                    "- Time per proposal: {} ms/proposal\n" +
                    "Cumulative stage times:\n" +
                    "- Expanding predicates: {} ms\n" +
                    "- Expanding injections: {} ms\n" +
                    "- Expanding queries: {} ms\n" +
                    "- Expanding deterministically: {} ms",
                testName, testIndex,
                proposalCount,
                sort,
                textSize,
                astSize,

                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(completionTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(analysisTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(preparationTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(completionTime / proposalCount)),

                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingPredicatesTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingInjectionsTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingQueriesTime)),
                String.format("%2d", TimeUnit.NANOSECONDS.toMillis(expandingDeterministicTime))
            );
        }
    }
}
