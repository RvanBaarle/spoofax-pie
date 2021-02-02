package mb.tiger.statix.spoofax.task;

import mb.common.option.Option;
import mb.common.style.Styling;
import mb.jsglr.common.JSGLRTokens;
import mb.pie.api.ExecContext;
import mb.pie.api.ExecException;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import javax.inject.Inject;
import java.io.IOException;

@mb.tiger.statix.spoofax.TigerScope
public class TigerStyle implements TaskDef<Supplier<Option<JSGLRTokens>>, Option<Styling>> {
    private final mb.tiger.statix.TigerStyler styler;

    @Inject public TigerStyle(mb.tiger.statix.TigerStyler styler) {
        this.styler = styler;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerStyle";
    }

    @Override
    public Option<Styling> exec(ExecContext context, Supplier<Option<JSGLRTokens>> tokensSupplier) throws ExecException, IOException, InterruptedException {
        return context.require(tokensSupplier).map(t -> styler.style(t.tokens));
    }
}
