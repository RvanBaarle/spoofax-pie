package mb.tiger.statix.spoofax.task;

import mb.constraint.common.ConstraintAnalyzer.MultiFileResult;
import mb.constraint.common.ConstraintAnalyzerContext;
import mb.constraint.common.ConstraintAnalyzerException;
import mb.constraint.pie.ConstraintAnalyzeMultiTaskDef;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.stratego.common.StrategoRuntime;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;

@mb.tiger.statix.spoofax.TigerScope
public class TigerAnalyzeMulti extends ConstraintAnalyzeMultiTaskDef {
    private final mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer;
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject
    public TigerAnalyzeMulti(mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer, Provider<StrategoRuntime> strategoRuntimeProvider) {
        this.constraintAnalyzer = constraintAnalyzer;
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override
    public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerAnalyzeMulti";
    }

    @Override
    protected MultiFileResult analyze(ResourcePath root, HashMap<ResourceKey, IStrategoTerm> asts, ConstraintAnalyzerContext context) throws ConstraintAnalyzerException {
        return constraintAnalyzer.analyze(root, asts, context, strategoRuntimeProvider.get());
    }
}
