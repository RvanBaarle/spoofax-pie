package mb.sdf3.task;

import mb.common.result.Result;
import mb.pie.api.Interactivity;
import mb.pie.api.Supplier;
import mb.sdf3.Sdf3Scope;
import mb.stratego.pie.AstStrategoTransformTaskDef;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.util.Set;

@Sdf3Scope
public class Sdf3ToCompletionRuntime extends AstStrategoTransformTaskDef {
    @Inject public Sdf3ToCompletionRuntime(Sdf3GetStrategoRuntimeProvider getStrategoRuntimeProvider) {
        super(getStrategoRuntimeProvider, "module-to-new-cmp");
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public boolean shouldExecWhenAffected(Supplier<? extends Result<IStrategoTerm, ?>> input, Set<?> tags) {
        return tags.isEmpty() || tags.contains(Interactivity.NonInteractive);
    }
}

