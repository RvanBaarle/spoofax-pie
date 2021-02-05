package mb.tiger.statix.spoofax.task;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.jsglr1.pie.JSGLR1ParseTaskDef;
import mb.tiger.statix.TigerClassLoaderResources;
import mb.pie.api.ExecContext;
import mb.pie.api.stamp.resource.ResourceStampers;

import javax.inject.Inject;
import javax.inject.Provider;

@mb.tiger.statix.spoofax.TigerScope
public class TigerParse extends JSGLR1ParseTaskDef {
    private final TigerClassLoaderResources classLoaderResources;
    private final Provider<mb.tiger.statix.TigerParser> parserProvider;

    @Inject
    public TigerParse(
        TigerClassLoaderResources classLoaderResources,
        Provider<mb.tiger.statix.TigerParser> parserProvider
    ) {
        this.classLoaderResources = classLoaderResources;
        this.parserProvider = parserProvider;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerParse";
    }


    @Override protected Result<JSGLR1ParseOutput, JSGLR1ParseException> parse(ExecContext context, String text) throws Exception {
        context.require(classLoaderResources.tryGetAsLocalDefinitionResource("sdf.tbl"));
        context.require(classLoaderResources.tryGetAsLocalResource(getClass()), ResourceStampers.hashFile());
        context.require(classLoaderResources.tryGetAsLocalResource(mb.tiger.statix.TigerParser.class), ResourceStampers.hashFile());
        context.require(classLoaderResources.tryGetAsLocalResource(mb.tiger.statix.TigerParserFactory.class), ResourceStampers.hashFile());
        context.require(classLoaderResources.tryGetAsLocalResource(mb.tiger.statix.TigerParseTable.class), ResourceStampers.hashFile());
        final mb.tiger.statix.TigerParser parser = parserProvider.get();
        try {
            return Result.ofOk(parser.parse(text, "Module"));
        } catch(JSGLR1ParseException e) {
            return Result.ofErr(e);
        }
    }
}
