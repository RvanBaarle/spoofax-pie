package mb.spt.inputs.spoofax;

import mb.common.result.Result;
import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.pie.api.Pie;
import mb.pie.api.Task;
import mb.spt.ITestFragment;
import mb.spt.inputs.ITestExpectationInput;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * A parse result test expectation input.
 */
public final class ParseTestExpectationInput implements ITestExpectationInput<IStrategoTerm> {

    private final Pie pie = null;
//    private final Supplier<Result<IStrategoTerm, ?>> parseInput

    @Override
    public Result<IStrategoTerm, ? extends Exception> get(ITestFragment fragment) throws ExecException, InterruptedException {

        // TODO: Get the parse task for the LUT
        Task<Result<IStrategoTerm, ?>> parseTask = null;
        try (final MixedSession session = this.pie.newSession()) {
            return session.require(parseTask);
        }
    }

}
