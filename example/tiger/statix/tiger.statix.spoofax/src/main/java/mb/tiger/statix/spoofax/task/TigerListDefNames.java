package mb.tiger.statix.spoofax.task;

import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoUtil;
import mb.tiger.statix.spoofax.TigerScope;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import javax.inject.Provider;

@TigerScope
public class TigerListDefNames implements TaskDef<Supplier<? extends Result<IStrategoTerm, ?>>, Result<String, ?>> {
    private final Provider<StrategoRuntime> strategoRuntimeProvider;

    @Inject
    public TigerListDefNames(Provider<StrategoRuntime> strategoRuntimeProvider) {
        this.strategoRuntimeProvider = strategoRuntimeProvider;
    }

    @Override
    public String getId() { return "mb.tiger.spoofax.task.reusable.TigerListDefNames"; }

    @Override
    public Result<String, ?> exec(ExecContext context, Supplier<? extends Result<IStrategoTerm, ?>> astSupplier) throws Exception {
        return context.require(astSupplier)
            .mapCatching((ast) -> StrategoUtil.toString(strategoRuntimeProvider.get().invoke("list-of-def-names", ast), Integer.MAX_VALUE));
    }
}
