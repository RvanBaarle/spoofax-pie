package mb.statix.completions.bench;

import mb.statix.completions.TermCompleter;
import mb.statix.completions.bench.performance.BenchmarkStats;
import mb.statix.completions.bench.performance.CsvFile;
import mb.statix.completions.bench.performance.TestBenchmark;
import mb.statix.completions.bench.performance.TestGenerator;
import mb.statix.completions.bench.performance.TigerTestBenchmark;
import mb.statix.completions.bench.performance.TigerTestGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.apache.commons.cli.Options;
import org.spoofax.terms.TermFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

/** Main class. */
public final class Main {
    private Main() { /* Cannot be instantiated. */ }

    private static final SummaryGeneratingListener listener = new SummaryGeneratingListener();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Expected a command, one of: completeness, generate, run");
            System.exit(2);
        }
        String cmd = args[0];
        switch (cmd) {
            case "completeness":
                runCompletenessTests(skip1(args));
                break;
            case "generate":
                generateTestCases(skip1(args));
                break;
            case "run":
                runTestCases(skip1(args));
                break;
            default:
                System.err.println("Expected a command, one of: completeness, generate, run");
                System.exit(2);
                break;
        }
    }

    private static void runCompletenessTests(String[] args) throws IOException {
        final LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectPackage("mb.statix.completions.bench.completeness"))
            .filters(includeClassNamePatterns(".*Test"))
            .build();
        final Launcher launcher = LauncherFactory.create();
        final TestPlan testPlan = launcher.discover(request);   // This should be kept.
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        final TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        summary.printFailuresTo(new PrintWriter(System.err));
    }

    private static void generateTestCases(String[] args) throws IOException {
        final Options options = new Options();
        options.addOption(new Option("o", "output", true, "Output directory."));
        final CommandLine cmd = parseArgs(options, args);

        if (!cmd.hasOption("output")) {
            throw new IllegalArgumentException("Expected --output option, got nothing.");
        }
        final Path outputDirectory = Paths.get(cmd.getOptionValue("output"));

        final TestGenerator generator = new TigerTestGenerator(new TermFactory());
        generator.generateAll(outputDirectory);
    }

    private static void runTestCases(String[] args) throws IOException, InterruptedException {
        final Options options = new Options();
        options.addOption(new Option("i", "input", true, "Input directory."));
        options.addOption(new Option("f", "file", true, "Output file."));
        final CommandLine cmd = parseArgs(options, args);

        if (!cmd.hasOption("input")) {
            throw new IllegalArgumentException("Expected --input option, got nothing.");
        }
        final Path inputDirectory = Paths.get(cmd.getOptionValue("input"));

        if (!cmd.hasOption("file")) {
            throw new IllegalArgumentException("Expected --file option, got nothing.");
        }
        final Path outputFile = Paths.get(cmd.getOptionValue("file"));

        try (final CsvFile csv = CsvFile.create(outputFile)) {
            final TestBenchmark tester = new TigerTestBenchmark(csv, new TermFactory(), new TermCompleter());
            tester.testAll(inputDirectory);
        }
    }

    private static String[] skip1(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private static CommandLine parseArgs(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch(ParseException e) {
            System.err.println("Parsing command-line failed: " + e.getMessage());
            System.exit(2);
            return null;
        }
    }
}
