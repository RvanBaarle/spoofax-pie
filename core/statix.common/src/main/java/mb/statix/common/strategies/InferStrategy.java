package mb.statix.common.strategies;

import mb.sequences.Sequence;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;
import mb.strategies.AssertStrategy;
import mb.strategies.Strategy;
import org.metaborg.util.task.NullCancel;
import org.metaborg.util.task.NullProgress;

import java.util.Collections;
import java.util.List;

/**
 * Performs inference on the search state.
 *
 * NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.
 */
public final class InferStrategy implements Strategy<SolverContext, SolverState, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final InferStrategy instance = new InferStrategy();
    @SuppressWarnings("unchecked")
    public static InferStrategy getInstance() { return instance; }

    private InferStrategy() {}

    @Override
    public Sequence<SolverState> eval(SolverContext ctx, SolverState state) throws InterruptedException {
        // TODO: Delay computation until Sequence is queried?
        final SolverResult result = Solver.solve(
            ctx.getSpec(),
            state.getState(),
            state.getConstraints(),
            state.getDelays(),
            state.getCompleteness(),
            new NullDebugContext(),
            new NullProgress(),
            new NullCancel()
        );

        // NOTE: Call the isSuccessful() strategy on this result to ensure it has no errors.

        SolverState newState = SolverState.fromSolverResult(result, state.getExistentials());
        return Sequence.of(newState);
    }

    @Override
    public String getName() {
        return "infer";
    }

}
