package mb.spt.spoofax.task;

import mb.common.result.Result;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.spoofax.core.language.LanguageScope;
import mb.spt.ITestSuite;
import mb.spt.spoofax.SpoofaxTestSuiteExtractor;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

/**
 * Task to extract a test suite from an SPT test specification.
 */
@LanguageScope
public final class ExtractTestSuiteTask implements TaskDef<ExtractTestSuiteTask.Input, Result<ITestSuite, ?>> {

    /**
     * Input to the task.
     */
    public static class Input implements Serializable {
        public final ResourceKey resourceKey;
        public final Supplier<Result<IStrategoTerm, ?>> desugaredAstSupplier;

        public Input(ResourceKey resourceKey, Supplier<Result<IStrategoTerm, ?>> desugaredAstSupplier) {
            this.resourceKey = resourceKey;
            this.desugaredAstSupplier = desugaredAstSupplier;
        }

        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            final Input input = (Input)o;
            // @formatter:off
            return resourceKey.equals(input.resourceKey)
                && desugaredAstSupplier.equals(input.desugaredAstSupplier);
            // @formatter:on
        }

        @Override public int hashCode() {
            return Objects.hash(resourceKey, desugaredAstSupplier);
        }

        @Override public String toString() {
            return "Input(resourceKey=" + resourceKey + ", desugaredAstSupplier=" + desugaredAstSupplier + ')';
        }
    }

    private final Logger log;
    private final SpoofaxTestSuiteExtractor testSuiteExtractor;

    @Inject public ExtractTestSuiteTask(
        LoggerFactory loggerFactory,
        SpoofaxTestSuiteExtractor testSuiteExtractor
    ) {
        this.log = loggerFactory.create(ExtractTestSuiteTask.class);
        this.testSuiteExtractor = testSuiteExtractor;
    }

    @Override public String getId() {
        return ExtractTestSuiteTask.class.getName();
    }

    @Override public Result<ITestSuite, ?> exec(ExecContext context, Input input) throws Exception {
        return context.require(input.desugaredAstSupplier).map(
            ast -> testSuiteExtractor.extract(ast)
        );
//        final IStrategoTerm desugaredAst = context.require(input.desugaredAstSupplier);
//
//        try {
//            final ITestSuite testSuite = testSuiteExtractor.extract(desugaredAst);
//            return Result.ofOk(testSuite);
//        } catch (IllegalArgumentException e) {
//            return Result.ofErr(e);
//        }
    }

}
