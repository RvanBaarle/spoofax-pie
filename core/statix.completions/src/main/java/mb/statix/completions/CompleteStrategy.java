package mb.statix.completions;
//
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Multiset;
//import io.usethesource.capsule.Set;
//import mb.nabl2.terms.ITerm;
//import mb.nabl2.terms.ITermVar;
//import mb.statix.common.FocusedSolverState;
//import mb.statix.common.SolverContext;
//import mb.statix.common.SolverState;
//import mb.statix.common.StrategoPlaceholders;
//import mb.statix.common.strategies.Strategies;
//import mb.statix.common.strategies.Strategy;
//import mb.statix.constraints.CResolveQuery;
//import mb.statix.constraints.CUser;
//import org.checkerframework.checker.nullness.qual.Nullable;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//
//import static mb.statix.common.strategies.Strategies.*;
//import static mb.statix.common.strategies.Strategies.limit;
//import static mb.statix.search.CollectionExt.containsAny;
//import static mb.statix.strategies.search.SearchStrategies.*;

///**
// * Complete strategy.
// */
//public final class CompleteStrategy implements Strategy<SolverState, SolverState, SolverContext> {
//
//    private final ITermVar placeholderVar;
//
//    /**
//     * Initializes a new instance of the {@link CompleteStrategy} strategy.
//     *
//     * @param placeholderVar the placeholder variable to complete
//     */
//    public CompleteStrategy(ITermVar placeholderVar) {
//        this.placeholderVar = placeholderVar;
//    }
//
//    @Override
//    public List<SolverState> apply(SolverContext ctx, SolverState input) throws InterruptedException {
//        Strategy<FocusedSolverState<CUser>, SolverState, SolverContext> continuation = buildInnerCompletionStrategy(placeholderVar);
//        return distinct(
//            fixSet(
//                // Focus on a constraint that contains the var we're interested in
//                // Once the variable is no longer present, the focus will fail and the repeat will stop
//                seq(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
//                    .$(continuation)
//                    .$()
//            )
//        ).apply(ctx, input);
//    }
//
//    private Strategy<FocusedSolverState<CUser>, SolverState, SolverContext> buildInnerCompletionStrategy(ITermVar placeholderVar) {
//        return debug(seq(Strategies.<FocusedSolverState<CUser>, SolverContext>id())
//            // Expand the focussed rule
//            .$(expandRule())
//            // Perform inference
//            .$(infer())
//            // Remove states that have errors
//            .$(isSuccessful())
//            // Delay stuck queries
//            .$(delayStuckQueries())
//            // Reject when it results only in a placeholder
//            .$(assertThat(s -> !StrategoPlaceholders.isPlaceholder(s.project(placeholderVar))))
//            // Repeat until all fails:
//            // Focus on a query
//            .$(repeat(distinct(seq(limit(1, focus(CResolveQuery.class)))
//                // Expand the query into its results
//                .$(expandQuery())
//                // Perform inference
//                .$(infer())
//                // Remove states that have errors
//                .$(isSuccessful())
//                // Delay stuck queries
//                .$(delayStuckQueries())
//                .$()
//            )))
//            // TODO: Make distinct before trying this
//            // Perhaps make all strategies return sets?
//            .$(fixSet(
//                // Find the variables in the projection of the placeholder variable,
//                // and try to expand each variable to a single result if possible.
//                // FIXME: projecting and gathering variables every time is not very efficient
//                // we could do it once for each state
//                seq(limit(1, focus(CUser.class, (c, s) -> {
//                    final Multiset<ITermVar> innerVars = s.project(placeholderVar).getVars();
//                    return constraintContainsAnyVar(s, c, innerVars);
//                } )))
//                    // Expand the focussed rule, and ensure there is only one result
//                    .$(assertAll(
//                        seq(expandRule())
//                            // Perform inference
//                            .$(infer())
//                            // Remove states that have errors
//                            .$(isSuccessful())
//                            // Delay stuck queries
//                            .$(delayStuckQueries())
//                            .$(),
//                        ts -> {
//                            if (ts.size() == 1)
//                                // There is only one result (it is deterministic), so we allow
//                                return returnAndWrite("Accepted one result", true);
//                                // FIXME: This is incorrect: it will repeatedly determine that the first constructor is an injection,
//                                // and expand it infinitely.
////                        else if (StrategoPlaceholders.countCommonInjectionConstructors(ts.stream().map(s -> s.project(placeholderVar)).collect(Collectors.toList())) > 0)
////                            // They share common injection constructors, we allow
////                            return returnAndWrite("Accepted common injection constructors", true);
//                            else
//                                // Non-deterministic and no shared injection constructors, we reject
//                                return returnAndWrite("Rejected, got " + ts.size() + " results", false);
//                        }
//                    ))
//                    .$()
//            ))
//            // TODO: Reject if it result in a placeholder (mostly, if it's an injection of a placeholder, e.g. LValue2Exp(LValue-Plhdr())
//            .$(), s -> System.out.println(s));
//    }
//
//    private static boolean constraintContainsVar(SolverState state, CUser constraint, ITermVar var) {
//        return constraintContainsAnyVar(state, constraint, Collections.singletonList(var));
//    }
//
//    private static boolean constraintContainsAnyVar(SolverState state, CUser constraint, Collection<ITermVar> vars) {
//        @Nullable final ImmutableMap<ITermVar, ITermVar> existentials = state.getExistentials();
//        final ArrayList<ITermVar> projectedVars = new ArrayList<>(vars.size());
//        if (existentials != null) {
//            for (ITermVar var : vars) {
//                @Nullable ITermVar projected = existentials.get(var);
//                if(projected != null) {
//                    projectedVars.add(projected);
//                } else {
//                    projectedVars.add(var);
//                }
//            }
//        } else {
//            projectedVars.addAll(vars);
//        }
//        // We use the unifier to get all the variables in each of the argument to the constraint
//        // (or the constraint argument itself when there where no variables and the argument is a term var)
//        // and see if any match the var we're looking for.
//        for (ITerm arg : constraint.args()) {
//            final Set.Immutable<ITermVar> constraintVars = state.getState().unifier().getVars(arg);
//            final boolean match = !constraintVars.isEmpty() ? containsAny(constraintVars, projectedVars) : projectedVars.contains(arg);
//            if (match) return true;
//        }
//        return false;
//    }
//
//    private static boolean returnAndWrite(String msg, boolean value) {
//        System.out.println(msg);
//        return value;
//    }
//}
