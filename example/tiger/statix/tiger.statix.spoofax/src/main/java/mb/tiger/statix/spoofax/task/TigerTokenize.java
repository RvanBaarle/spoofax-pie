package mb.tiger.statix.spoofax.task;

import mb.common.option.Option;
import mb.jsglr.common.JSGLRTokens;
import mb.pie.api.ExecContext;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.IOException;

@mb.tiger.statix.spoofax.TigerScope
public class TigerTokenize implements TaskDef<ResourceKey, Option<JSGLRTokens>> {
    private final mb.tiger.statix.spoofax.task.TigerParse parse;

    @Inject public TigerTokenize(mb.tiger.statix.spoofax.task.TigerParse parse) {
        this.parse = parse;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerTokenize";
    }

    @Override
    public @Nullable Option<JSGLRTokens> exec(ExecContext context, ResourceKey key) throws IOException {
        return context.require(parse.createTokensSupplier(key)).ok();
    }
}
