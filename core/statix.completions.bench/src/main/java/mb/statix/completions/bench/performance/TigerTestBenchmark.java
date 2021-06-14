package mb.statix.completions.bench.performance;

import mb.statix.common.StatixSpec;
import mb.statix.completions.TermCompleter;
import mb.statix.completions.bench.completeness.CompletenessTest;
import org.spoofax.interpreter.terms.ITermFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Test benchmark for Tiger.
 */
public final class TigerTestBenchmark extends TestBenchmark {

    protected static final String TESTPATH = "/tiger/";

    public TigerTestBenchmark(CsvFile csv, ITermFactory factory, TermCompleter completer) {
        super(factory, StatixSpec.fromClassLoaderResources(CompletenessTest.class, TESTPATH + "tiger.stx.aterm"),
            "static-semantics", "programOk", csv, completer);
    }

    @Override
    public void testAll(Path inputDirectory) throws IOException, InterruptedException {
        for (final String testName : TigerTestGenerator.tests) {
            System.out.println("Testing " + testName + "...");
            runTestSuite(inputDirectory, testName);
        }
        System.out.println("Done!");
    }

}
