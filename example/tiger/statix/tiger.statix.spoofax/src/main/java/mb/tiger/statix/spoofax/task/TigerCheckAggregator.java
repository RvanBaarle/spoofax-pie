package mb.tiger.statix.spoofax.task;

import mb.common.message.KeyedMessages;
import mb.common.message.Messages;
import mb.common.message.KeyedMessagesBuilder;
import mb.common.util.UncheckedException;
import mb.pie.api.ExecContext;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.hierarchical.HierarchicalResource;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.match.ResourceMatcher;
import mb.resource.hierarchical.walk.ResourceWalker;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

@mb.tiger.statix.spoofax.TigerScope
public class TigerCheckAggregator implements TaskDef<TigerCheckAggregator.Input, @Nullable KeyedMessages> {
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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Input input = (Input) o;
            return root.equals(input.root) &&
                walker.equals(input.walker) &&
                matcher.equals(input.matcher);
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

    private final mb.tiger.statix.spoofax.task.TigerCheck check;

    @Inject public TigerCheckAggregator(mb.tiger.statix.spoofax.task.TigerCheck check){
        this.check = check;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerCheckAggregator";
    }

    @Override public @Nullable KeyedMessages exec(ExecContext context, Input input) throws Exception {
        final HierarchicalResource root = context.require(input.root, ResourceStampers.modifiedDirRec(input.walker, input.matcher));
        final KeyedMessagesBuilder builder = new KeyedMessagesBuilder();
        try {
            root.walk(input.walker, input.matcher).forEach(file -> {
                try {
                    final ResourceKey fileKey = file.getKey();
                    final @Nullable Messages messages = context.require(check, fileKey);
                    if(messages != null) {
                        builder.addMessages(fileKey, messages);
                    }
                } catch(Exception e) {
                    throw new UncheckedException(e);
                }
            });
        } catch(UncheckedException e) {
            throw e.getCause();
        }

        return builder.build();
    }
}