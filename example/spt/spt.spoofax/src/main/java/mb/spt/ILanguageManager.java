package mb.spt;

import mb.common.result.Result;
import mb.pie.api.Task;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Manages languages.
 */
public interface ILanguageManager {

    Task<Result<IStrategoTerm, ?>> getParseTask(String languageId);

//    Task<Result<IStrategoTerm, ?>> getAnalyzeTask(String languageId);

}
