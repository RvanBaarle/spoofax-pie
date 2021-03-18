package mb.statix.strategies;

import mb.sequences.Seq;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.IConstraint;
import mb.strategies.AbstractStrategy;


/**
 * Unfocuses any constraint.
 */
public final class UnfocusStrategy<C extends IConstraint> extends AbstractStrategy<SolverContext, SelectedConstraintSolverState<C>, SolverState> {

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
    public Seq<SolverState> eval(SolverContext ctx, SelectedConstraintSolverState<C> input) {
        return Seq.of(input.getInnerState());
    }

}
