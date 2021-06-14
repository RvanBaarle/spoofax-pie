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
        "appel/test09.tig",                     // excluded because of semantic error
        "appel/test10.tig",                     // excluded because of semantic error
        "appel/test11.tig",                     // excluded because of semantic error
        "appel/test12.tig",
        "appel/test13.tig",                     // excluded because of semantic error
        "appel/test14.tig",                     // excluded because of semantic error
        "appel/test15.tig",                     // excluded because of semantic error
        "appel/test16.tig",
        "appel/test17.tig",                     // excluded because of semantic error
        "appel/test18.tig",                     // excluded because of semantic error
        "appel/test19.tig",                     // excluded because of semantic error
        "appel/test20.tig",                     // excluded because of semantic error
        "appel/test21.tig",                     // excluded because of semantic error
        "appel/test22.tig",                     // excluded because of semantic error
        "appel/test23.tig",                     // excluded because of semantic error
        "appel/test24.tig",                     // excluded because of semantic error
        "appel/test25.tig",                     // excluded because of semantic error
        "appel/test26.tig",                     // excluded because of semantic error
        "appel/test27.tig",
        "appel/test28.tig",                     // excluded because of semantic error
        "appel/test29.tig",                     // excluded because of semantic error
        "appel/test30.tig",
        "appel/test31.tig",                     // excluded because of semantic error
        "appel/test32.tig",                     // excluded because of semantic error
        "appel/test33.tig",                     // excluded because of semantic error
        "appel/test34.tig",                     // excluded because of semantic error
        "appel/test35.tig",                     // excluded because of semantic error
        "appel/test36.tig",                     // excluded because of semantic error
        "appel/test37.tig",
        "appel/test38.tig",                     // excluded because of semantic error
        "appel/test39.tig",                     // excluded because of semantic error
        "appel/test40.tig",                     // excluded because of semantic error
        "appel/test41.tig",
        "appel/test42.tig",
        "appel/test43.tig",                     // excluded because of semantic error
        "appel/test44.tig",
        "appel/test45.tig",                     // excluded because of semantic error
        "appel/test47.tig",
        "appel/test48.tig",

        "examples/arith.tig",
        "examples/fac-error.tig",               // excluded because of semantic error
        "examples/fact-anf.tig",
        "examples/fact-anf2.tig",
        "examples/fact-resolvetest.tig",
        "examples/for.tig",
        "examples/let-binding.tig",             // excluded because of semantic error
        "examples/list-type.tig",               // excluded because of semantic error
        "examples/nested.tig",
        "examples/point.tig",
        "examples/queens.tig",
        "examples/rec-types.tig",
        "examples/record-errors.tig",           // excluded because of semantic error
        "examples/recursion.tig",
        "examples/redeclarations.tig",          // excluded because of semantic error
        "examples/tiny.tig",
        "examples/tinyrec.tig",
        "examples/tinytiny.tig",
        "examples/type-dec.tig",                // excluded because of semantic error
        "examples/verytiny.tig",
        "examples/while-break.tig",
        "examples/while.tig",

        "microbenchmarks/branching.tig",        // excluded because of semantic error
        "microbenchmarks/break-intensive.tig",  // excluded because of semantic error
        "microbenchmarks/list.tig",             // excluded because of semantic error
        "microbenchmarks/permute.tig",          // excluded because of semantic error
        "microbenchmarks/queens-looped.tig",    // excluded because of semantic error
        "microbenchmarks/queens2.tig",          // excluded because of semantic error
        "microbenchmarks/sieve.tig",            // excluded because of semantic error
        "microbenchmarks/towers.tig",           // excluded because of semantic error
        "microbenchmarks/var-local.tig",        // excluded because of semantic error
        "microbenchmarks/var-parent.tig",       // excluded because of semantic error
        "microbenchmarks/while-call.tig",
        "microbenchmarks/while-loop.tig",       // excluded because of semantic error

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
        "tests/binding/binding02.tig",          // excluded because of semantic error
        "tests/binding/binding03.tig",          // excluded because of semantic error
        "tests/binding/binding04.tig",          // excluded because of semantic error
        "tests/binding/binding05.tig",          // excluded because of semantic error
        "tests/binding/binding06.tig",          // excluded because of semantic error
        "tests/binding/binding07.tig",          // excluded because of semantic error
        "tests/binding/binding08.tig",
        "tests/binding/binding10.tig",          // excluded because of semantic error
        "tests/binding/bindings09.tig",
        "tests/binding/bindings11.tig",

        "tests/operators/operator-test01.tig",
        "tests/operators/operators-test02.tig",
        "tests/operators/operators-test03.tig",

        "tests/statements/stat-test01.tig",
        "tests/statements/stat-test02.tig",
        "tests/statements/stat-test03.tig",

        "xmpl/a.tig",
        "xmpl/arrays-tiny.tig",
        "xmpl/arrays.tig",
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
        "xmpl/typecheck-error1.tig",            // excluded because of semantic error

        "xmpl2/error1.tig",                     // excluded because of semantic error
        "xmpl2/matrix.tig",                     // excluded because of semantic error
        "xmpl2/mytest1.tig",                    // excluded because of semantic error
        "xmpl2/mytest10.tig",
        "xmpl2/mytest11.tig",
        "xmpl2/mytest12.tig",
        "xmpl2/mytest13.tig",
        "xmpl2/mytest14.tig",
        "xmpl2/mytest15.tig",
        "xmpl2/mytest16.tig",
        "xmpl2/mytest2.tig",                    // excluded because of semantic error
        "xmpl2/mytest3.tig",                    // excluded because of semantic error
        "xmpl2/mytest5.tig",
        "xmpl2/mytest6.tig",
        "xmpl2/mytest7.tig",
        "xmpl2/pp-test1.tig",                   // excluded because of semantic error
        "xmpl2/tiny1.tig"                       // excluded because of semantic error
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
