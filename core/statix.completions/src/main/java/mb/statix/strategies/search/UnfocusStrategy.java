package mb.statix.strategies.search;

import mb.statix.common.FocusedSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;
import mb.statix.solver.IConstraint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Unfocuses any constraint.
 */
public final class UnfocusStrategy<C extends IConstraint> implements Strategy<FocusedSolverState<C>, SolverState, SolverContext> {

    @Override
    public List<SolverState> apply(SolverContext ctx, FocusedSolverState<C> input) {
        return Collections.singletonList(input.getInnerState());

    }

    @Override
    public String toString() {
        return "unfocus";
    }

}
