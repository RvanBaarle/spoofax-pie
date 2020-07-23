package mb.spt.spoofax.task;

import mb.jsglr1.common.JSGLR1ParseException;
import mb.pie.api.Task;
import mb.spoofax.core.language.LanguageScope;
import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.ResourceKey;
import mb.resource.ResourceService;
import mb.resource.WritableResource;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.LanguageScope;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.ShowFeedback;
import mb.spt.ITestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.metaborg.sdf2table.io.ParseTableIO;
import org.metaborg.sdf2table.parsetable.ParseTable;
import org.metaborg.sdf2table.parsetable.ParseTableConfiguration;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@LanguageScope
public class RunTestsTaskDef implements TaskDef<RunTestsTaskDef.Args, CommandFeedback> {

    public static class Args implements Serializable {
        public final ResourceKey input;
        public final ResourceKey output;

        public Args(ResourceKey input, ResourceKey output) {
            this.input = input;
            this.output = output;
        }

        @Override public boolean equals(@Nullable Object obj) {
            if(this == obj) return true;
            if(obj == null || getClass() != obj.getClass()) return false;
            final Args other = (Args)obj;
            return this.input.equals(other.input)
                && this.output.equals(other.output);
        }

        @Override public int hashCode() {
            return Objects.hash(
                input,
                output
            );
        }

        @Override public String toString() {
            return input + " -> " + output;
        }
    }

    private final ResourceService resourceService;
    private final ExtractTestSuiteTask extractTestSuiteTask;
    private final SptDesugar sptDesugar;
    private final SptParse sptParse;

    @Inject public RunTestsTaskDef(ResourceService resourceService, ExtractTestSuiteTask extractTestSuiteTask, SptDesugar sptDesugar, SptParse sptParse) {
        this.resourceService = resourceService;
        this.extractTestSuiteTask = extractTestSuiteTask;
        this.sptDesugar = sptDesugar;
        this.sptParse = sptParse;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, Args args) throws Exception {

        final Task<Result<ITestSuite, ?>> testSuiteTask = extractTestSuiteTask.createTask(new ExtractTestSuiteTask.Input(
            args.input,
            sptDesugar.createSupplier(sptParse.createAstSupplier(args.input))
        ));
        final Result<ITestSuite, ?> testSuiteResult = context.require(testSuiteTask);

        return testSuiteResult
            .mapCatching(testSuite -> {
                // TODO: Run the test suite

                final WritableResource outputResource = resourceService.getWritableResource(args.output);
                try (final OutputStream outputStream = outputResource.openWrite()) {
                    try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                        try (final BufferedWriter writer = new BufferedWriter(outputStreamWriter)) {
                            // TODO: Write the test suite result
                            writer.write(testSuite.toString());
                        }
                    }
                }
                context.provide(outputResource, ResourceStampers.hashFile());
                return outputResource.getKey();
            })
            .mapOrElse(f -> CommandFeedback.of(ShowFeedback.showFile(f)), e -> CommandFeedback.ofTryExtractMessagesFrom(e, args.input));
    }
}
