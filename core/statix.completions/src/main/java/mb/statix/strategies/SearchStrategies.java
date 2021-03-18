package mb.statix.strategies;

import mb.nabl2.terms.ITermVar;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.InferStrategy;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.solver.IConstraint;
import mb.strategies.Strategy;

import java.util.function.BiPredicate;


/**
 * Convenience functions for creating search strategies.
 */
public final class SearchStrategies {

    /**
     * Delays stuck queries in the search state.
     *
     * @return the resulting strategy
     */
    public static Strategy<SolverContext, SolverState, SolverState> delayStuckQueries() {
        return DelayStuckQueriesStrategy.getInstance();
    }

    /**
     * Expands queries in the search state.
     *
     * @return the resulting strategy
     */
    public static Strategy<SolverContext, SelectedConstraintSolverState<CResolveQuery>, SolverState> expandQueryConstraint() {
        return ExpandQueryStrategy.getInstance();
    }

    /**
     * Expands rules in the search state.
     *
     * @return the resulting strategy
     */
    public static Strategy<SolverContext, SelectedConstraintSolverState<CUser>, SolverState> expandPredicateConstraint(ITermVar v) {
        return ExpandRuleStrategy.getInstance().apply(v);
    }

    /**
     * Focuses the search state on a particular constraint.
     *
     * @param constraintClass the class of constraints to focus on
     * @param predicate the predicate indicating which constraint to focus on
     * @param <C> the type of constraints to focus on
     * @return the resulting strategy
     */
    public static <C extends IConstraint> Strategy<SolverContext, SolverState, SelectedConstraintSolverState<C>> selectConstraints(Class<C> constraintClass, BiPredicate<C, SolverState> predicate) {
        return SelectStrategy.<C>getInstance().apply(constraintClass, predicate);
    }

    /**
     * Focuses the search state on a particular constraint, unconditionally.
     *
     * @param constraintClass the class of constraints to focus on
     * @param <C> the type of constraints to focus on
     * @return the resulting strategy
     */
    public static <C extends IConstraint> Strategy<SolverContext, SolverState, SelectedConstraintSolverState<C>> selectConstraints(Class<C> constraintClass) {
        return selectConstraints(constraintClass, (c, s) -> true);
    }

    /**
     * Performs inference on the search strategy.
     *
     * @return the resulting strategy
     */
    public static Strategy<SolverContext, SolverState, SolverState> infer() {
        return InferStrategy.getInstance();
    }

//    /**
//     * Performs inference on the search strategy, and asserts that it succeeded.
//     *
//     * @return the resulting strategy
//     */
//    public static Strategy<SolverContext, SolverState, SolverState> inferSuccess() {
//        return InferSuccessStrategy.getInstance();
//    }
//
//    /**
//     * Search strategy that only succeeds if the search state has no errors.
//     *
//     * @return the resulting strategy
//     */
//    public static Strategy<SolverContext, SolverState, SolverState> isSuccessful() { return IsSuccessfulStrategy.getInstance(); }

//    /**
//     * Removes Ast ID constraints that where not solved.
//     *
//     * @return the resulting strategy
//     */
//    public static Strategy<SolverContext, SolverState, SolverState> removeAstIdConstraints() {
//        return new RemoveAstIdConstraintsStrategy();
//    }

    /**
     * Unfocuses.
     *
     * @return the resulting strategy
     */
    public static <C extends IConstraint> Strategy<SolverContext, SelectedConstraintSolverState<C>, SolverState> unfocus() {
        return UnfocusStrategy.getInstance();
    }
}
