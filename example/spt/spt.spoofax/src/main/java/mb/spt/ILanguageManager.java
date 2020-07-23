package mb.spt;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.pie.api.Supplier;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Manages languages.
 */
public interface ILanguageManager {

    TaskDef<Supplier<String>, Result<JSGLR1ParseOutput, JSGLR1ParseException>> getParseTaskDef(String languageId);

//    Task<Result<IStrategoTerm, ?>> getAnalyzeTask(String languageId);

}
