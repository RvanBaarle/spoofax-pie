package mb.statix.common.strategies;

import mb.sequences.Computation;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.log.NullDebugContext;
import mb.statix.solver.persistent.Solver;
import mb.statix.solver.persistent.SolverResult;
import mb.strategies.Strategy;
import org.metaborg.util.task.NullCancel;
import org.metaborg.util.task.NullProgress;

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
    public Computation<SolverState> apply(SolverContext ctx, SolverState state) {
        return () -> {
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

            return SolverState.fromSolverResult(result, state.getExistentials());
        };
    }

    @Override
    public String getName() {
        return "infer";
    }

}
