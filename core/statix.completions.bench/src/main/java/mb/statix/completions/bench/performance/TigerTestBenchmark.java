package mb.statix.completions.bench.performance;

import mb.statix.common.StatixSpec;
import mb.statix.completions.TermCompleter;
import mb.statix.completions.bench.completeness.CompletenessTest;
import org.spoofax.interpreter.terms.ITermFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Test benchmark for Tiger.
 */
public final class TigerTestBenchmark extends TestBenchmark {

    protected static final String TESTPATH = "/tiger/";

    private final List<String> tests = Arrays.asList(
        "test1.tig",
        "test2.tig",
        "test3.tig",
        "test4.tig",
        "test5.tig",

        "appel/test01.tig",
        "appel/test02.tig",
        "appel/test03.tig",
        "appel/test04.tig",
        "appel/test05.tig",
        "appel/test06.tig",
        "appel/test07.tig",
        "appel/test08.tig",
        "appel/test12.tig",
        "appel/test16.tig",
        "appel/test27.tig",
        "appel/test30.tig",
        "appel/test37.tig",
        "appel/test41.tig",
        "appel/test42.tig",
        "appel/test44.tig",
        "appel/test47.tig",
        "appel/test48.tig",

        "examples/arith.tig",
        "examples/fact-anf.tig",
        "examples/fact-anf2.tig",
        "examples/fact-resolvetest.tig",
        "examples/for.tig",
        "examples/nested.tig",
        "examples/point.tig",
        "examples/queens.tig",
        "examples/rec-types.tig",
        "examples/recursion.tig",
        "examples/tiny.tig",
        "examples/tinyrec.tig",
        "examples/tinytiny.tig",
        "examples/verytiny.tig",
        "examples/while.tig",
        "examples/while-break.tig",

        "microbenchmarks/while-call.tig",

        "natives/chr.tig",
        "natives/concat.tig",
        "natives/exit.tig",
        "natives/flush.tig",
        "natives/getchar.tig",
        "natives/not.tig",
        "natives/ord.tig",
        "natives/print.tig",
        "natives/size.tig",
        "natives/substring.tig",

        "tests/binding/binding01.tig",
        "tests/binding/binding08.tig",
        "tests/binding/bindings09.tig",
        "tests/binding/bindings11.tig",

        "tests/operators/operator-test01.tig",
        "tests/operators/operators-test02.tig",
        "tests/operators/operators-test03.tig",

        "tests/statements/stat-test01.tig",
        "tests/statements/stat-test02.tig",
        "tests/statements/stat-test03.tig",

        "xmpl/a.tig",
        "xmpl/arrays.tig",
        "xmpl/arrays-tiny.tig",
        "xmpl/aterm.tig",
        "xmpl/break.tig",
        "xmpl/eval-test1.tig",
        "xmpl/eval-test2.tig",
        "xmpl/even-odd.tig",
        "xmpl/extract.tig",
        "xmpl/fac.tig",
        "xmpl/for.tig",
        "xmpl/function.tig",
        "xmpl/let.tig",
        "xmpl/merge.tig",
        "xmpl/multi-arg.tig",
        "xmpl/mytest4.tig",
        "xmpl/mytest5.tig",
        "xmpl/nestedfunctions.tig",
        "xmpl/prettyprint.tig",
        "xmpl/queens.tig",
        "xmpl/rec1.tig",
        "xmpl/record.tig",
        "xmpl/renaming1.tig",
        "xmpl/seq.tig",
        "xmpl/trtest1.tig",
        "xmpl/trtest2.tig",
        "xmpl/trtest3.tig",
        "xmpl/trtest4.tig",
        "xmpl2/mytest5.tig",
        "xmpl2/mytest6.tig",
        "xmpl2/mytest7.tig",
        "xmpl2/mytest10.tig",
        "xmpl2/mytest11.tig",
        "xmpl2/mytest12.tig",
        "xmpl2/mytest13.tig",
        "xmpl2/mytest14.tig",
        "xmpl2/mytest15.tig",
        "xmpl2/mytest16.tig"
    );

    public TigerTestBenchmark(CsvFile csv, ITermFactory factory, TermCompleter completer) {
        super(factory, StatixSpec.fromClassLoaderResources(CompletenessTest.class, TESTPATH + "tiger.stx.aterm"),
            "static-semantics", "programOk", csv, completer);
    }

    @Override
    public void testAll(Path inputDirectory) throws IOException, InterruptedException {
        for (final String testName : tests) {
            System.out.println("Testing " + testName + "...");
            runTestSuite(inputDirectory, testName);
        }
        System.out.println("Done!");
    }

}
