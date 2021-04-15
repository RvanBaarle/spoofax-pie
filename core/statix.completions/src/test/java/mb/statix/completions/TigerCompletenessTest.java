package mb.statix.completions;

import io.usethesource.capsule.Map;
import mb.jsglr.common.MoreTermUtils;
import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.IStringTerm;
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
import mb.statix.constraints.messages.IMessage;
import mb.statix.constraints.messages.MessageKind;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests that the completion algorithm is complete.
 * For a given AST, it must be able to regenerate that AST in a number of completion steps,
 * when presented with the AST with a hole in it.
 */
@SuppressWarnings("SameParameterValue")
public class TigerCompletenessTest extends CompletenessTest {

    private static final String TIGER_SPEC_PATH = TESTPATH + "/spec.aterm";
    private static final String TIGER_SPEC_SIMPLE1_PATH = TESTPATH + "/simple1/spec.aterm";
    private static final String TIGER_CSV_OUTPUT_PATH = "/Users/daniel/repos/spoofax3/devenv/spoofax.pie/core/statix.completions/src/test/resources/mb/statix/completions/tiger";
    private static final String ATERM_EXT = ".aterm";
    private static final String CSV_EXT = ".csv";
    private static final String ORIGINAL_EXT = "";

    // TODO: Enable
    @TestFactory
    @Disabled
    public List<DynamicTest> completenessTests() {
        return Arrays.asList(
            // Tiger
//            tigerTest("/test1.tig"),
//            tigerTest("/test2.tig"),
//            tigerTest("/test3.tig"),
//            tigerTest("/test4.tig"),

//            tigerTest("/appel/test01.tig"),
//            tigerTest("/appel/test02.tig"),
////            tigerTest("/appel/test03.tig"),
////            tigerTest("/appel/test04.tig"),
////            tigerTest("/appel/test05.tig"),
////            tigerTest("/appel/test06.tig"),
////            tigerTest("/appel/test07.tig"),
//            tigerTest("/appel/test08.tig"),
//            tigerTest("/appel/test12.tig"),
//            tigerTest("/appel/test16.tig"),
//            tigerTest("/appel/test27.tig"),
//            tigerTest("/appel/test30.tig"),
//            tigerTest("/appel/test37.tig"),
//            tigerTest("/appel/test41.tig"),
////            tigerTest("/appel/test42.tig"),
//            tigerTest("/appel/test44.tig"),
//            tigerTest("/appel/test47.tig"),
////            tigerTest("/appel/test48.tig")
//
            tigerTest("/examples/arith.tig"),
//            tigerTest("/examples/fact-anf.tig"),      // interrupted
            tigerTest("/examples/fact-anf2.tig"),
            tigerTest("/examples/fact-resolvetest.tig"),
            tigerTest("/examples/for.tig"),
            tigerTest("/examples/nested.tig"),
//            tigerTest("/examples/point.tig"),         // interrupted
//            tigerTest("/examples/queens.tig"),        // failed
//            tigerTest("/examples/rec-types.tig"),     // interrupted
//            tigerTest("/examples/recursion.tig"),     // failed
//            tigerTest("/examples/tiny.tig"),          // interrupted
//            tigerTest("/examples/tinyrec.tig"),       // interrupted
            tigerTest("/examples/tinytiny.tig"),
            tigerTest("/examples/verytiny.tig"),
            tigerTest("/examples/while.tig"),
            tigerTest("/examples/while-break.tig")
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
//            tigerTest("/xmpl/merge.tig"),
//            tigerTest("/xmpl/multi-arg.tig"),
//            tigerTest("/xmpl/mytest4.tig"),
//            tigerTest("/xmpl/mytest5.tig"),
//            tigerTest("/xmpl/nestedfunctions.tig"),
//            tigerTest("/xmpl/prettyprint.tig"),
//            tigerTest("/xmpl/queens.tig"),
//            tigerTest("/xmpl/rec1.tig"),
//            tigerTest("/xmpl/record.tig"),
//            tigerTest("/xmpl/renaming1.tig"),
//            tigerTest("/xmpl/seq.tig"),
//            tigerTest("/xmpl/trtest1.tig"),
//            tigerTest("/xmpl/trtest2.tig"),
//            tigerTest("/xmpl/trtest3.tig"),
//            tigerTest("/xmpl/trtest4.tig"),
//            tigerTest("/xmpl/typecheck-error1.tig"),
//
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
        return completenessTest(testPath + expectedTermPath + ATERM_EXT, inputPath, specPath, specName, csvPath, rootRuleName);
    }

}
