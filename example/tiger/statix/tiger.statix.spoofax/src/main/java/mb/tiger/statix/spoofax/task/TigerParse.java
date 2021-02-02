package mb.tiger.statix.spoofax.task;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.jsglr1.pie.JSGLR1ParseTaskDef;
import javax.inject.Inject;
import javax.inject.Provider;

@mb.tiger.statix.spoofax.TigerScope
public class TigerParse extends JSGLR1ParseTaskDef {
    private final Provider<mb.tiger.statix.TigerParser> parserProvider;

    @Inject
    public TigerParse(Provider<mb.tiger.statix.TigerParser> parserProvider) {
        this.parserProvider = parserProvider;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerParse";
    }

    @Override protected Result<JSGLR1ParseOutput, JSGLR1ParseException> parse(String text) throws InterruptedException {
        final mb.tiger.statix.TigerParser parser = parserProvider.get();
        try {
            return Result.ofOk(parser.parse(text, "Module"));
        } catch(JSGLR1ParseException e) {
            return Result.ofErr(e);
        }
    }
}
