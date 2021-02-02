package mb.tiger.statix.spoofax.task;

import mb.common.message.KeyedMessages;
import mb.common.message.KeyedMessagesBuilder;
import mb.common.message.Messages;
import mb.common.message.Severity;
import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.pie.api.TaskDef;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.hierarchical.HierarchicalResource;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.match.ResourceMatcher;
import mb.resource.hierarchical.walk.ResourceWalker;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Objects;

@mb.tiger.statix.spoofax.TigerScope
public class TigerCheckMulti implements TaskDef<TigerCheckMulti.Input, KeyedMessages> {
    public static class Input implements Serializable {
        public final ResourcePath root;
        public final ResourceWalker walker;
        public final ResourceMatcher matcher;

        public Input(
            ResourcePath root,
            ResourceWalker walker,
            ResourceMatcher matcher
        ) {
            this.root = root;
            this.walker = walker;
            this.matcher = matcher;
        }

        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            final Input input = (Input)o;
            return root.equals(input.root) && walker.equals(input.walker) && matcher.equals(input.matcher);
        }

        @Override public int hashCode() {
            return Objects.hash(root, walker, matcher);
        }

        @Override public String toString() {
            return "Input{" +
                "root=" + root +
                ", walker=" + walker +
                ", matcher=" + matcher +
                '}';
        }
    }

        mb.tiger.statix.spoofax.task.TigerParse parse;
        mb.tiger.statix.spoofax.task.TigerAnalyzeMulti analyze;

    @Inject public TigerCheckMulti(
        mb.tiger.statix.spoofax.task.TigerParse parse,
        mb.tiger.statix.spoofax.task.TigerAnalyzeMulti analyze
    ) {
        this.parse = parse;
        this.analyze = analyze;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerCheckMulti";
    }

    @Override public KeyedMessages exec(ExecContext context, Input input) throws IOException {
        final KeyedMessagesBuilder messagesBuilder = new KeyedMessagesBuilder();
        final HierarchicalResource root = context.require(input.root, ResourceStampers.modifiedDirRec(input.walker, input.matcher));
        try {
            root.walk(input.walker, input.matcher).forEach(file -> {
                final ResourcePath filePath = file.getPath();
                final Messages messages = context.require(parse.createMessagesSupplier(filePath));
                messagesBuilder.addMessages(filePath, messages);
            });
        } catch(UncheckedIOException e) {
            throw e.getCause();
        }
        final mb.tiger.statix.spoofax.task.TigerAnalyzeMulti.Input analyzeInput = new mb.tiger.statix.spoofax.task.TigerAnalyzeMulti.Input(input.root, input.walker, input.matcher, parse.createRecoverableAstFunction());
        final Result<mb.tiger.statix.spoofax.task.TigerAnalyzeMulti.Output, ?> analysisResult = context.require(analyze, analyzeInput);
        analysisResult
            .ifOk(output -> {
                messagesBuilder.addMessages(output.result.messages);
                messagesBuilder.addMessages(output.messagesFromAstProviders);
            })
            .ifErr(e -> messagesBuilder.addMessage("Project-wide analysis failed", e, Severity.Error, input.root));
        return messagesBuilder.build();
    }
}
