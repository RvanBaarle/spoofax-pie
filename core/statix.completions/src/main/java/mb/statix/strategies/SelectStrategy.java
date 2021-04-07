package mb.statix.strategies;

import mb.sequences.Computation;
import mb.sequences.Seq;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.IConstraint;
import mb.strategies.AbstractStrategy2;
import mb.strategies.DebugStrategy;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


/**
 * Selects constraints that match the given predicate.
 */
public final class SelectStrategy<C extends IConstraint> extends AbstractStrategy2<SolverContext, Class<C>, BiPredicate<C, SolverState>, SolverState, SelectedConstraintSolverState<C>> {

    @SuppressWarnings("rawtypes")
    private static final SelectStrategy instance = new SelectStrategy();
    @SuppressWarnings("unchecked")
    public static <C extends IConstraint> SelectStrategy<C> getInstance() { return (SelectStrategy<C>)instance; }

    private SelectStrategy() {}

    @Override
    public String getName() {
        return "select";
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "constraintClass";
            case 1: return "predicate";
            default: return super.getParamName(index);
        }
    }

    @Override
    protected Seq<SelectedConstraintSolverState<C>> innerEval(SolverContext ctx, Class<C> constraintClass, BiPredicate<C, SolverState> predicate, SolverState input) {

//        Optional<C> focus = input.getConstraints().stream()
//            .filter(c -> constraintClass.isAssignableFrom(c.getClass()))
//            .map(c -> (C)c)
//            .filter(c -> predicate.test(c, input))
//            .findFirst();
        //noinspection unchecked
        List<SelectedConstraintSolverState<C>> selected = input.getConstraints().stream()
            .filter(c -> constraintClass.isAssignableFrom(c.getClass()))
            .map(c -> (C)c)
            .filter(c -> predicate.test(c, input))
            .map(c -> new SelectedConstraintSolverState<>(input, c))
            .collect(Collectors.toList());
//            .collect(Collectors.toList());
//        if (focus.isPresent()) {
//            System.out.println("Focus: " + focus.get());
//        } else {
//            System.out.println("Focus: NONE");
//        }
        if (DebugStrategy.debug) {
            System.out.println("SELECTED: ");
            for(SelectedConstraintSolverState<C> s : selected) {
                System.out.println("- " + s.getSelected());
            }
        }

        return Seq.from(selected);
//        return focus.map(c -> Seq.of(new FocusedSolverState<>(input, c))).orElseGet(Seq::empty);
    }

}
