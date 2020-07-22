package mb.spt;

import mb.common.result.Result;
import mb.pie.api.Task;
import org.spoofax.interpreter.terms.IStrategoTerm;
import mb.sdf3.spoofax.task.Sdf3Parse;

public final class FixedLanguageManager implements ILanguageManager {
    @Override
    public Task<Result<IStrategoTerm, ?>> getParseTask(String languageId) {
        switch (languageId) {
            case "sdf3":
                //Sdf3Parse
                break;
        }
        return null;
    }
}
