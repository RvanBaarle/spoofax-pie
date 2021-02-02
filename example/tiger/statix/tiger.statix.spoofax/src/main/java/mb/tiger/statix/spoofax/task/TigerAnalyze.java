package mb.tiger.statix.spoofax.task;

import mb.constraint.common.ConstraintAnalyzer.SingleFileResult;
import mb.constraint.common.ConstraintAnalyzerContext;
import mb.constraint.common.ConstraintAnalyzerException;
import mb.constraint.pie.ConstraintAnalyzeTaskDef;
import mb.resource.ResourceKey;
import mb.stratego.common.StrategoRuntime;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import javax.inject.Provider;

@mb.tiger.statix.spoofax.TigerScope
public class TigerAnalyze extends ConstraintAnalyzeTaskDef {
    private final mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject
    public TigerAnalyze(mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer, Provider<StrategoRuntime> strategoRuntimeProvider) {
        this.constraintAnalyzer = constraintAnalyzer;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override
    public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerAnalyze";
    }

    @Override
    protected SingleFileResult analyze(ResourceKey resource, IStrategoTerm ast, ConstraintAnalyzerContext context) throws ConstraintAnalyzerException {
        return constraintAnalyzer.analyze(resource, ast, context, strategoRuntimeProvider.get());
    }
}
