package mb.spt.spoofax.task;

import mb.spoofax.core.language.LanguageScope;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.pie.AstStrategoTransformTaskDef;

import javax.inject.Inject;
import javax.inject.Provider;

@LanguageScope
public class SptDesugar extends AstStrategoTransformTaskDef {
    @Inject public SptDesugar(Provider<StrategoRuntime> strategoRuntimeProvider) {
        super(strategoRuntimeProvider, "desugar-before");
    }

    @Override public String getId() {
        return getClass().getName();
    }
}
