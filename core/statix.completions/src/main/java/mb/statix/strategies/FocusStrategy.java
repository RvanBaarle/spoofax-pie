package mb.statix.strategies;

import mb.sequences.Seq;
import mb.statix.common.FocusedSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.solver.IConstraint;
import mb.strategies.AbstractStrategy2;
import mb.strategies.Strategy2;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Focus on a single constraint.
 */
public final class FocusStrategy<C extends IConstraint> extends AbstractStrategy2<SolverContext, Class<C>, BiPredicate<C, SolverState>, SolverState, FocusedSolverState<C>> {

    @SuppressWarnings("rawtypes")
    private static final FocusStrategy instance = new FocusStrategy();
    @SuppressWarnings("unchecked")
    public static <C extends IConstraint> FocusStrategy<C> getInstance() { return (FocusStrategy<C>)instance; }

    private FocusStrategy() {}

    @Override
    public String getName() {
        return "focus";
    }

    @Override
    public Seq<FocusedSolverState<C>> eval(SolverContext ctx, Class<C> constraintClass, BiPredicate<C, SolverState> predicate, SolverState input) {
        //noinspection unchecked
//        Optional<C> focus = input.getConstraints().stream()
//            .filter(c -> constraintClass.isAssignableFrom(c.getClass()))
//            .map(c -> (C)c)
//            .filter(c -> predicate.test(c, input))
//            .findFirst();
        Stream<C> focus = input.getConstraints().stream()
            .filter(c -> constraintClass.isAssignableFrom(c.getClass()))
            .map(c -> (C)c)
            .filter(c -> predicate.test(c, input));
//            .collect(Collectors.toList());
//        if (focus.isPresent()) {
//            System.out.println("Focus: " + focus.get());
//        } else {
//            System.out.println("Focus: NONE");
//        }
        return Seq.from(focus.map(c -> new FocusedSolverState<>(input, c)).collect(Collectors.toList()));
//        return focus.map(c -> Seq.of(new FocusedSolverState<>(input, c))).orElseGet(Seq::empty);
    }

}
