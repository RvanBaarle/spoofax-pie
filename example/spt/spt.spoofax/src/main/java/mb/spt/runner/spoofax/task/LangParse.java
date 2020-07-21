package mb.spt.runner.spoofax.task;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.jsglr1.pie.JSGLR1ParseTaskDef;
import mb.spoofax.core.language.LanguageScope;
import mb.spt.SptParser;

import javax.inject.Inject;
import javax.inject.Provider;

@LanguageScope
public class LangParse extends JSGLR1ParseTaskDef {
    private final Provider<SptParser> parserProvider;

    @Inject
    public LangParse(Provider<SptParser> parserProvider) {
        this.parserProvider = parserProvider;
    }

    public String getId() {
        return "mb.spt.spoofax.task.SptParse";
    }

    protected Result<JSGLR1ParseOutput, JSGLR1ParseException> parse(String text) throws InterruptedException {
        SptParser parser = (SptParser)this.parserProvider.get();

        try {
            return Result.ofOk(parser.parse(text, "TestSuite"));
        } catch (JSGLR1ParseException var4) {
            return Result.ofErr(var4);
        }
    }
}
