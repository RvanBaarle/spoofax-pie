package mb.statix.strategies;

import mb.sequences.Sequence;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.IConstraint;
import mb.strategies.Strategy;

import static mb.statix.strategies.SearchStrategies.*;
import static mb.strategies.Strategies.seq;


/**
 * Performs inference and ensures it succeeds, then delays stuck queries.
 */
public final class InferSuccessStrategy implements Strategy<SolverContext, SolverState, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final InferSuccessStrategy instance = new InferSuccessStrategy();
    @SuppressWarnings("unchecked")
    public static InferSuccessStrategy getInstance() { return (InferSuccessStrategy)instance; }

    private InferSuccessStrategy() {}

    @Override
    public Sequence<SolverState> eval(SolverContext ctx, SolverState input) throws InterruptedException {
        return
            // Perform inference
            seq(infer())
            // Remove states that have errors
            .$(isSuccessful())
            // Delay stuck queries
            .$(delayStuckQueries())
            .$()
            .eval(ctx, input);
    }
}
