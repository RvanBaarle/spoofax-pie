package mb.statix.strategies;

import mb.sequences.Sequence;
import mb.statix.common.FocusedSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.IConstraint;
import mb.strategies.Strategy;

import java.util.Collections;
import java.util.List;


/**
 * Unfocuses any constraint.
 */
public final class UnfocusStrategy<C extends IConstraint> implements Strategy<SolverContext, FocusedSolverState<C>, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final UnfocusStrategy instance = new UnfocusStrategy();
    @SuppressWarnings("unchecked")
    public static <C extends IConstraint> UnfocusStrategy<C> getInstance() { return (UnfocusStrategy<C>)instance; }

    private UnfocusStrategy() {}

    @Override
    public String getName() {
        return "unfocus";
    }

    @Override
    public Sequence<SolverState> eval(SolverContext ctx, FocusedSolverState<C> input) {
        return Sequence.of(input.getInnerState());
    }

}
