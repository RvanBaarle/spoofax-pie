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


    // TODO: Enable
    @TestFactory
    //@Disabled
    public List<DynamicTest> completenessTests() {
        return Arrays.asList(
            // Tiger
            tigerTest("/test1.tig.aterm"),
            tigerTest("/test2.tig.aterm"),
            tigerTest("/test3.tig.aterm"),
            tigerTest("/test4.tig.aterm"),

            tigerTest("/appel/test01.tig.aterm"),
            tigerTest("/appel/test02.tig.aterm"),
            tigerTest("/appel/test03.tig.aterm"),
            tigerTest("/appel/test04.tig.aterm"),
            tigerTest("/appel/test05.tig.aterm"),
            tigerTest("/appel/test06.tig.aterm"),
            tigerTest("/appel/test07.tig.aterm"),
            tigerTest("/appel/test08.tig.aterm"),
            tigerTest("/appel/test12.tig.aterm"),
            tigerTest("/appel/test16.tig.aterm"),
            tigerTest("/appel/test27.tig.aterm"),
            tigerTest("/appel/test30.tig.aterm"),
            tigerTest("/appel/test37.tig.aterm"),
            tigerTest("/appel/test41.tig.aterm"),
            tigerTest("/appel/test42.tig.aterm"),
            tigerTest("/appel/test44.tig.aterm"),
            tigerTest("/appel/test47.tig.aterm"),
            tigerTest("/appel/test48.tig.aterm"),

            tigerTest("/examples/arith.tig.aterm"),
            tigerTest("/examples/fact-anf.tig.aterm"),
            tigerTest("/examples/fact-anf2.tig.aterm"),
            tigerTest("/examples/fact-resolvetest.tig.aterm"),
            tigerTest("/examples/for.tig.aterm"),
            tigerTest("/examples/nested.tig.aterm"),
            tigerTest("/examples/point.tig.aterm"),
            tigerTest("/examples/queens.tig.aterm"),
            tigerTest("/examples/rec-types.tig.aterm"),
            tigerTest("/examples/recursion.tig.aterm"),
            tigerTest("/examples/tiny.tig.aterm"),
            tigerTest("/examples/tinyrec.tig.aterm"),
            tigerTest("/examples/tinytiny.tig.aterm"),
            tigerTest("/examples/varytiny.tig.aterm"),
            tigerTest("/examples/while.tig.aterm"),
            tigerTest("/examples/while-break.tig.aterm"),

            tigerTest("/microbenchmarks/while-call.tig.aterm"),

            tigerTest("/natives/chr.tig.aterm"),
            tigerTest("/natives/concat.tig.aterm"),
            tigerTest("/natives/exit.tig.aterm"),
            tigerTest("/natives/flush.tig.aterm"),
            tigerTest("/natives/getchar.tig.aterm"),
            tigerTest("/natives/not.tig.aterm"),
            tigerTest("/natives/ord.tig.aterm"),
            tigerTest("/natives/print.tig.aterm"),
            tigerTest("/natives/size.tig.aterm"),
            tigerTest("/natives/substring.tig.aterm"),

            tigerTest("/tests/binding/binding01.tig.aterm"),
            tigerTest("/tests/binding/binding08.tig.aterm"),
            tigerTest("/tests/binding/bindings09.tig.aterm"),
            tigerTest("/tests/binding/bindings11.tig.aterm"),


            tigerTest("/tests/operators/operator-test01.tig.aterm"),
            tigerTest("/tests/operators/operators-test02.tig.aterm"),
            tigerTest("/tests/operators/operators-test03.tig.aterm"),

            tigerTest("/tests/statements/stat-test01.tig.aterm"),
            tigerTest("/tests/statements/stat-test02.tig.aterm"),
            tigerTest("/tests/statements/stat-test03.tig.aterm"),

            tigerTest("/xmpl/a.tig.aterm"),
            tigerTest("/xmpl/arrays.tig.aterm"),
            tigerTest("/xmpl/arrays-tiny.tig.aterm"),
            tigerTest("/xmpl/aterm.tig.aterm"),
            tigerTest("/xmpl/break.tig.aterm"),
            tigerTest("/xmpl/eval-test1.tig.aterm"),
            tigerTest("/xmpl/eval-test2.tig.aterm"),
            tigerTest("/xmpl/even-odd.tig.aterm"),
            tigerTest("/xmpl/extract.tig.aterm"),
            tigerTest("/xmpl/fac.tig.aterm"),
            tigerTest("/xmpl/for.tig.aterm"),
            tigerTest("/xmpl/function.tig.aterm"),
            tigerTest("/xmpl/let.tig.aterm"),
            tigerTest("/xmpl/merge.tig.aterm"),
            tigerTest("/xmpl/multi-arg.tig.aterm"),
            tigerTest("/xmpl/mytest4.tig.aterm"),
            tigerTest("/xmpl/mytest5.tig.aterm"),
            tigerTest("/xmpl/nestedfunctions.tig.aterm"),
            tigerTest("/xmpl/prettyprint.tig.aterm"),
            tigerTest("/xmpl/queens.tig.aterm"),
            tigerTest("/xmpl/rec1.tig.aterm"),
            tigerTest("/xmpl/record.tig.aterm"),
            tigerTest("/xmpl/renaming1.tig.aterm"),
            tigerTest("/xmpl/seq.tig.aterm"),
            tigerTest("/xmpl/trtest1.tig.aterm"),
            tigerTest("/xmpl/trtest2.tig.aterm"),
            tigerTest("/xmpl/trtest3.tig.aterm"),
            tigerTest("/xmpl/trtest4.tig.aterm"),
            tigerTest("/xmpl/typecheck-error1.tig.aterm"),

            tigerTest("/xmpl2/mytest5.tig.aterm"),
            tigerTest("/xmpl2/mytest6.tig.aterm"),
            tigerTest("/xmpl2/mytest7.tig.aterm"),
            tigerTest("/xmpl2/mytest10.tig.aterm"),
            tigerTest("/xmpl2/mytest11.tig.aterm"),
            tigerTest("/xmpl2/mytest12.tig.aterm"),
            tigerTest("/xmpl2/mytest13.tig.aterm"),
            tigerTest("/xmpl2/mytest14.tig.aterm"),
            tigerTest("/xmpl2/mytest15.tig.aterm"),
            tigerTest("/xmpl2/mytest16.tig.aterm")
        );
    }

    private DynamicTest tigerTest(String expectedTermPath) {
        final String testPath = TESTPATH + "/tiger";
        final String inputPath = testPath + "/input.tig.aterm";
        final String specPath = testPath + "/tiger.stx.aterm";
        final String specName = "static-semantics";
        final String rootRuleName = "programOk";
        return completenessTest(testPath + expectedTermPath, inputPath, specPath, specName, rootRuleName);
    }

}
