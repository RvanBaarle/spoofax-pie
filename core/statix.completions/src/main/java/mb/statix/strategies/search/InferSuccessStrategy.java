package mb.statix.strategies.search;

import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;

import java.util.List;

import static mb.statix.common.strategies.Strategies.seq;
import static mb.statix.strategies.search.SearchStrategies.*;

/**
 * Performs inference and ensures it succeeds, then delays stuck queries.
 */
public final class InferSuccessStrategy implements Strategy<SolverState, SolverState, SolverContext> {
    @Override
    public List<SolverState> apply(SolverContext ctx, SolverState input) throws InterruptedException {
        return
            // Perform inference
            seq(infer())
            // Remove states that have errors
            .$(isSuccessful())
            // Delay stuck queries
            .$(delayStuckQueries())
            .$()
            .apply(ctx, input);
    }
}
