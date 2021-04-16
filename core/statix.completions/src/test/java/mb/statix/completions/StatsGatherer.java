package mb.statix.completions;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Gathers statistics on a test.
 */
public class StatsGatherer {

    private static final SLF4JLoggerFactory loggerFactory = new SLF4JLoggerFactory();
    private static final Logger log = loggerFactory.create(CompletenessTest.class);

    private final String csvPath;
    private String testName;
    private long prepStartTime;
    private long analyzeStartTime;
    private long analyzeEndTime;
    private final List<RoundStats> rounds = new ArrayList<>();
    @Nullable private RoundStats currentRound = null;

    public StatsGatherer(String csvPath) {
        this.csvPath = csvPath;
    }

    public static class RoundStats {
        @CsvBindByName
        //@CsvBindByPosition(position = 0)
        private long roundStartTime;

        @CsvBindByName
        //@CsvBindByPosition(position = 1)
        private long roundEndTime;

        @CsvBindByName
        //@CsvBindByPosition(position = 2)
        private int literalsInserted = 0;

        public long getRoundStartTime() {
            return roundStartTime;
        }

        public void setRoundStartTime(long roundStartTime) {
            this.roundStartTime = roundStartTime;
        }

        public long getRoundEndTime() {
            return roundEndTime;
        }

        public void setRoundEndTime(long roundEndTime) {
            this.roundEndTime = roundEndTime;
        }

        public int getLiteralsInserted() {
            return literalsInserted;
        }

        public void setLiteralsInserted(int literalsInserted) {
            this.literalsInserted = literalsInserted;
        }

        public long getRoundTime() {
            return this.roundEndTime - this.roundStartTime;
        }
    }

    /**
     * Starts the test.
     */
    public void startTest(String testName) {
        this.testName = testName;
        this.prepStartTime = System.nanoTime();
    }

    public void startInitialAnalysis() {
        this.analyzeStartTime = System.nanoTime();
    }
    public void endInitialAnalysis() { this.analyzeEndTime = System.nanoTime(); }
    /**
     * Starts a round of completion.
     */
    public void startRound() {
        assert this.currentRound == null;
        long roundStartTime = System.nanoTime();

        this.currentRound = new RoundStats();
        this.currentRound.setRoundStartTime(roundStartTime);
    }

    /**
     * Skips a round of completion.
     */
    public void skipRound() {
        assert this.currentRound != null;
        this.currentRound = null;
    }

    /**
     * Ends a round of completion.
     */
    public void endRound() {
        assert this.currentRound != null;
        this.currentRound.setRoundEndTime(System.nanoTime());
        this.rounds.add(this.currentRound);
        this.currentRound = null;
    }

    /**
     * Ends the test.
     */
    public void endTest() {
        logSummary();
    }

    /**
     * Indicates that a literal was inserted.
     */
    public void insertedLiteral() {
        assert this.currentRound != null;
        this.currentRound.setLiteralsInserted(this.currentRound.getLiteralsInserted() + 1);
    }

    /**
     * Logs the summary.
     */
    private void logSummary() {
        long totalPrepareTime = analyzeStartTime - prepStartTime;
        long totalAnalyzeTime = analyzeEndTime - analyzeStartTime;
        long totalCompleteTime = sumLong(rounds, r -> r.roundEndTime - r.roundStartTime);
        int literalsInserted = sumInt(rounds, r -> r.literalsInserted);
        long avgDuration = totalCompleteTime / rounds.size();
        //final String testFileName = getTestFileName();
        writeCsv(Paths.get(csvPath));
        log.info("TEST DONE!\n" +
                "Completed {} steps in {} ms, avg. {} ms/step.\n" +
                "Preparation: {} ms, initial analysis: {} ms.\n" +
                "Inserted {} literals.",
            rounds.size(),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalCompleteTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(avgDuration)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalPrepareTime)),
            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalAnalyzeTime)),
            literalsInserted
        );
    }

    /**
     * Writes a CSV file with the stats.
     * @param path the path
     */
    private void writeCsv(Path path) {
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.append("sep=,\n");
            final StatefulBeanToCsv<RoundStats> csv = new StatefulBeanToCsvBuilder<RoundStats>(writer)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .build();
            csv.write(this.rounds);
        } catch(CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException ex) {
            throw new RuntimeException("Unable to write CSV.", ex);
        }
    }

    private String getTestFileName() {
        return this.testName
            .replaceAll("[^a-zA-Z0-9_]", "_")
            .replaceAll("__", "_")
            .replaceFirst("^[_]+", "")
            + ".csv";
    }

    private int sumInt(List<RoundStats> roundStats, Function<RoundStats, Integer> projection) {
        int sum = 0;
        for (RoundStats roundStat : roundStats) {
            Integer value = projection.apply(roundStat);
            if (value != null) {
                sum += value;
            }
        }
        return sum;
    }

    private long sumLong(List<RoundStats> roundStats, Function<RoundStats, Long> projection) {
        long sum = 0;
        for (RoundStats roundStat : roundStats) {
            Long value = projection.apply(roundStat);
            if (value != null) {
                sum += value;
            }
        }
        return sum;
    }

}