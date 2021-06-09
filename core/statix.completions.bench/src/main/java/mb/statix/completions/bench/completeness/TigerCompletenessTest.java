package mb.statix.completions.bench.completeness;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Tests that the completion algorithm is complete.
 * For a given AST, it must be able to regenerate that AST in a number of completion steps,
 * when presented with the AST with a hole in it.
 */
@SuppressWarnings("SameParameterValue")
public class TigerCompletenessTest extends CompletenessTest {

    private static final String TIGER_SPEC_PATH = TESTPATH + "/spec.aterm";
    private static final String TIGER_SPEC_SIMPLE1_PATH = TESTPATH + "/simple1/spec.aterm";
    private static final String TIGER_CSV_OUTPUT_PATH = "output/";
//    private static final String TIGER_CSV_OUTPUT_PATH = "/Users/daniel/repos/spoofax3/devenv-cc/spoofax.pie/core/statix.completions/src/test/resources/mb/statix/completions/tiger";
    private static final String ATERM_EXT = ".aterm";
    private static final String CSV_EXT = ".csv";
    private static final String ORIGINAL_EXT = "";

    // TODO: Enable
    @TestFactory
//    @Disabled
    public List<DynamicTest> completenessTests() {
        return Arrays.asList(
            // Tiger
            tigerTest("/test1.tig"),
            tigerTest("/test2.tig")
//            tigerTest("/test3.tig"),
//            tigerTest("/test4.tig"),
//            tigerTest("/test5.tig"),
//
//            tigerTest("/appel/test01.tig"),
//            tigerTest("/appel/test02.tig"),
//            tigerTest("/appel/test03.tig"),
//            tigerTest("/appel/test04.tig"),
//            tigerTest("/appel/test05.tig"),
//            tigerTest("/appel/test06.tig"),
//            tigerTest("/appel/test07.tig"),
//            tigerTest("/appel/test08.tig"),
//            tigerTest("/appel/test12.tig"),
//            tigerTest("/appel/test16.tig"),
//            tigerTest("/appel/test27.tig"),
//            tigerTest("/appel/test30.tig"),
//            tigerTest("/appel/test37.tig"),
//            tigerTest("/appel/test41.tig"),
//            tigerTest("/appel/test42.tig"),
//            tigerTest("/appel/test44.tig"),
//            tigerTest("/appel/test47.tig"),
//            tigerTest("/appel/test48.tig"),
//
//            tigerTest("/examples/arith.tig"),
//            tigerTest("/examples/fact-anf.tig"),
//            tigerTest("/examples/fact-anf2.tig"),
//            tigerTest("/examples/fact-resolvetest.tig"),
//            tigerTest("/examples/for.tig"),
//            tigerTest("/examples/nested.tig"),
//            tigerTest("/examples/point.tig"),
//            tigerTest("/examples/queens.tig"),
//            tigerTest("/examples/rec-types.tig"),
//            tigerTest("/examples/recursion.tig"),
//            tigerTest("/examples/tiny.tig"),
//            tigerTest("/examples/tinyrec.tig"),
//            tigerTest("/examples/tinytiny.tig"),
//            tigerTest("/examples/verytiny.tig"),
//            tigerTest("/examples/while.tig"),
//            tigerTest("/examples/while-break.tig"),
//
//            tigerTest("/microbenchmarks/while-call.tig"),
//
//            tigerTest("/natives/chr.tig"),
//            tigerTest("/natives/concat.tig"),
//            tigerTest("/natives/exit.tig"),
//            tigerTest("/natives/flush.tig"),
//            tigerTest("/natives/getchar.tig"),
//            tigerTest("/natives/not.tig"),
//            tigerTest("/natives/ord.tig"),
//            tigerTest("/natives/print.tig"),
//            tigerTest("/natives/size.tig"),
//            tigerTest("/natives/substring.tig"),
//
//            tigerTest("/tests/binding/binding01.tig"),
//            tigerTest("/tests/binding/binding08.tig"),
//            tigerTest("/tests/binding/bindings09.tig"),
//            tigerTest("/tests/binding/bindings11.tig"),
//
//
//            tigerTest("/tests/operators/operator-test01.tig"),
//            tigerTest("/tests/operators/operators-test02.tig"),
//            tigerTest("/tests/operators/operators-test03.tig"),
//
//            tigerTest("/tests/statements/stat-test01.tig"),
//            tigerTest("/tests/statements/stat-test02.tig"),
//            tigerTest("/tests/statements/stat-test03.tig"),
//
//            tigerTest("/xmpl/a.tig"),
//            tigerTest("/xmpl/arrays.tig"),
//            tigerTest("/xmpl/arrays-tiny.tig"),
//            tigerTest("/xmpl/aterm.tig"),
//            tigerTest("/xmpl/break.tig"),
//            tigerTest("/xmpl/eval-test1.tig"),
//            tigerTest("/xmpl/eval-test2.tig"),
//            tigerTest("/xmpl/even-odd.tig"),
//            tigerTest("/xmpl/extract.tig"),
//            tigerTest("/xmpl/fac.tig"),
//            tigerTest("/xmpl/for.tig"),
//            tigerTest("/xmpl/function.tig"),
//            tigerTest("/xmpl/let.tig"),
//            tigerTest("/xmpl/merge.tig"),             // timed out
//            tigerTest("/xmpl/multi-arg.tig"),
//            tigerTest("/xmpl/mytest4.tig"),
//            tigerTest("/xmpl/mytest5.tig"),
//            tigerTest("/xmpl/nestedfunctions.tig"),
//            tigerTest("/xmpl/prettyprint.tig"),       // timed out
//            tigerTest("/xmpl/queens.tig"),
//            tigerTest("/xmpl/rec1.tig"),
//            tigerTest("/xmpl/record.tig"),
//            tigerTest("/xmpl/renaming1.tig"),
//            tigerTest("/xmpl/seq.tig"),
//            tigerTest("/xmpl/trtest1.tig"),
//            tigerTest("/xmpl/trtest2.tig"),
//            tigerTest("/xmpl/trtest3.tig"),
//            tigerTest("/xmpl/trtest4.tig"),
////            tigerTest("/xmpl/typecheck-error1.tig"),  // erroneous
//            tigerTest("/xmpl2/mytest5.tig"),
//            tigerTest("/xmpl2/mytest6.tig"),
//            tigerTest("/xmpl2/mytest7.tig"),
//            tigerTest("/xmpl2/mytest10.tig"),
//            tigerTest("/xmpl2/mytest11.tig"),
//            tigerTest("/xmpl2/mytest12.tig"),
//            tigerTest("/xmpl2/mytest13.tig"),
//            tigerTest("/xmpl2/mytest14.tig"),
//            tigerTest("/xmpl2/mytest15.tig"),
//            tigerTest("/xmpl2/mytest16.tig")
        );
    }

    private DynamicTest tigerTest(String expectedTermPath) {
        final String testPath = TESTPATH + "/tiger";
        final String inputPath = testPath + "/input.tig.aterm";
        final String specPath = testPath + "/tiger.stx.aterm";
        final String specName = "static-semantics";
        final String rootRuleName = "programOk";
        final String csvPath = TIGER_CSV_OUTPUT_PATH + expectedTermPath + CSV_EXT;
        try {
            Files.createDirectories(Paths.get(TIGER_CSV_OUTPUT_PATH));
        } catch(IOException e) {
            // Ignored
            e.printStackTrace();
        }
        return completenessTest(testPath, testPath + expectedTermPath + ATERM_EXT, inputPath, specPath, specName, csvPath, rootRuleName);
    }

}
