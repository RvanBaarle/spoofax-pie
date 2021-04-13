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
        //noinspection ArraysAsListWithZeroOrOneArgument
        return Arrays.asList(
//            completenessTest(TESTPATH + "/simple1/test1.aterm", TESTPATH + "/simple1/test1.input.aterm", TIGER_SPEC_SIMPLE1_PATH, "statics", "programOK"),
//            completenessTest(TESTPATH + "/test1.aterm", TESTPATH + "/test1.input.aterm", TIGER_SPEC_PATH, "static-semantics", "programOk"),
//            completenessTest(TESTPATH + "/test2.aterm", TESTPATH + "/test2.input.aterm", TIGER_SPEC_PATH, "static-semantics", "programOk"),
//            completenessTest(TESTPATH + "/test3.aterm", TESTPATH + "/test3.input.aterm", TIGER_SPEC_PATH, "static-semantics", "programOk"),
//            completenessTest(TESTPATH + "/test3.aterm", TESTPATH + "/test3_2.input.aterm", TIGER_SPEC_PATH, "static-semantics", "programOk"),
//            completenessTest(TESTPATH + "/test4.aterm", TESTPATH + "/test4.input.aterm", TIGER_SPEC_PATH, "static-semantics", "programOk"),
            // Tiger
            tigerTest("/test01.tig.aterm"),
            tigerTest("/test02.tig.aterm"),
            tigerTest("/test03.tig.aterm"),
            tigerTest("/test04.tig.aterm")
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
