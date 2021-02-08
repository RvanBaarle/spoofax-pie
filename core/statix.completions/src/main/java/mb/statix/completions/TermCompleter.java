package mb.statix.completions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.common.strategies.Strategy;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.statix.common.FocusedSolverState;
import mb.statix.common.strategies.Strategies;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static mb.statix.search.CollectionExt.containsAny;
import static mb.statix.strategies.search.SearchStrategies.*;
import static mb.statix.common.strategies.Strategies.*;


/**
 * The term completer.
 */
public final class TermCompleter {

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderVar the var of the placeholder to complete
     * @return the resulting completion proposals
     */
    public List<CompletionSolverProposal> complete(SolverContext ctx, SolverState state, ITermVar placeholderVar) throws InterruptedException {
        ITerm termInUnifier = state.getState().unifier().findRecursive(placeholderVar);
        if (!termInUnifier.equals(placeholderVar)) {
            // The variable we're looking for is already in the unifier
            return Collections.singletonList(new CompletionSolverProposal(state, termInUnifier));
        } else {
            // The variable we're looking for is not in the unifier
            return completeNodes(ctx, state, placeholderVar).stream().map(s -> new CompletionSolverProposal(s, s.project(placeholderVar))).collect(Collectors.toList());
        }
    }

    /**
     * Completes the specified constraint.
     *
     * @param ctx the search context
     * @param state the initial search state
     * @param placeholderVar the var of the placeholder to complete
     * @return the resulting states
     */
    public List<SolverState> completeNodes(SolverContext ctx, SolverState state, ITermVar placeholderVar) throws InterruptedException {
        return new CompleteStrategy(placeholderVar).apply(ctx, state);
//        return buildCompletionStrategy(placeholderVar).apply(ctx, state);
    }

    private Strategy<SolverState, SolverState, SolverContext> buildCompletionStrategy(ITermVar placeholderVar) {
        Strategy<FocusedSolverState<CUser>, SolverState, SolverContext> continuation = buildInnerCompletionStrategy(placeholderVar);
        return distinct(
            fixSet(
                // Focus on a constraint that contains the var we're interested in
                // Once the variable is no longer present, the focus will fail and the repeat will stop
                seq(limit(1, focus(CUser.class, (c, s) -> constraintContainsVar(s, c, placeholderVar))))
                    .$(continuation)
                    .$()
            )
        );
    }

    private Strategy<FocusedSolverState<CUser>, SolverState, SolverContext> buildInnerCompletionStrategy(ITermVar placeholderVar) {
        return debug(seq(Strategies.<FocusedSolverState<CUser>, SolverContext>id())
            // Expand the focussed rule
            .$(expandRule())
            // Perform inference
            .$(infer())
            // Remove states that have errors
            .$(isSuccessful())
            // Delay stuck queries
            .$(delayStuckQueries())
            // Reject when it results only in a placeholder
            .$(assertThat(s -> !StrategoPlaceholders.isPlaceholder(s.project(placeholderVar))))
            // Repeat until all fails:
            // Focus on a query
            .$(repeat(distinct(seq(limit(1, focus(CResolveQuery.class)))
                // Expand the query into its results
                .$(expandQuery())
                // Perform inference
                .$(infer())
                // Remove states that have errors
                .$(isSuccessful())
                // Delay stuck queries
                .$(delayStuckQueries())
                .$()
            )))
            // TODO: Make distinct before trying this
            // Perhaps make all strategies return sets?
            .$(fixSet(
                // Find the variables in the projection of the placeholder variable,
                // and try to expand each variable to a single result if possible.
                // FIXME: projecting and gathering variables every time is not very efficient
                // we could do it once for each state
                seq(limit(1, focus(CUser.class, (c, s) -> {
                    final Multiset<ITermVar> innerVars = s.project(placeholderVar).getVars();
                    return constraintContainsAnyVar(s, c, innerVars);
                } )))
                // Expand the focussed rule, and ensure there is only one result
                .$(assertAll(
                    seq(expandRule())
                    // Perform inference
                    .$(infer())
                    // Remove states that have errors
                    .$(isSuccessful())
                    // Delay stuck queries
                    .$(delayStuckQueries())
                    .$(),
                    ts -> {
                        if (ts.size() == 1)
                            // There is only one result (it is deterministic), so we allow
                            return returnAndWrite("Accepted one result", true);
                        // FIXME: This is incorrect: it will repeatedly determine that the first constructor is an injection,
                        // and expand it infinitely.
//                        else if (StrategoPlaceholders.countCommonInjectionConstructors(ts.stream().map(s -> s.project(placeholderVar)).collect(Collectors.toList())) > 0)
//                            // They share common injection constructors, we allow
//                            return returnAndWrite("Accepted common injection constructors", true);
                        else
                            // Non-deterministic and no shared injection constructors, we reject
                            return returnAndWrite("Rejected, got " + ts.size() + " results", false);
                    }
                ))
                .$()
            ))
            // TODO: Reject if it result in a placeholder (mostly, if it's an injection of a placeholder, e.g. LValue2Exp(LValue-Plhdr())
            .$(), s -> System.out.println(s));
    }

    private static boolean returnAndWrite(String msg, boolean value) {
        System.out.println(msg);
        return value;
    }

    private static boolean constraintContainsVar(SolverState state, CUser constraint, ITermVar var) {
        return constraintContainsAnyVar(state, constraint, Collections.singletonList(var));
    }

    private static boolean constraintContainsAnyVar(SolverState state, CUser constraint, Collection<ITermVar> vars) {
        @Nullable final ImmutableMap<ITermVar, ITermVar> existentials = state.getExistentials();
        final ArrayList<ITermVar> projectedVars = new ArrayList<>(vars.size());
        if (existentials != null) {
            for (ITermVar var : vars) {
                @Nullable ITermVar projected = existentials.get(var);
                if(projected != null) {
                    projectedVars.add(projected);
                } else {
                    projectedVars.add(var);
                }
            }
        } else {
            projectedVars.addAll(vars);
        }
        // We use the unifier to get all the variables in each of the argument to the constraint
        // (or the constraint argument itself when there where no variables and the argument is a term var)
        // and see if any match the var we're looking for.
        for (ITerm arg : constraint.args()) {
            final Set.Immutable<ITermVar> constraintVars = state.getState().unifier().getVars(arg);
            final boolean match = !constraintVars.isEmpty() ? containsAny(constraintVars, projectedVars) : projectedVars.contains(arg);
            if (match) return true;
        }
        return false;
    }



//    private static boolean isVarUnassigned(SolverState state, ITermVar var) {
//        return state.getState().unifier().findRecursive(var) instanceof ITermVar;
//    }

//    private static ITerm project(ITermVar placeholderVar, SolverState s) {
//        return s.getState().unifier().findRecursive(placeholderVar);
//    }

    private static ITerm accept(ITerm term) {
        return term;
    }

    /**
     * A completion solver result.
     */
    public final static class CompletionSolverProposal {
        private final SolverState newState;
        private final ITerm term;

        public CompletionSolverProposal(SolverState newState, ITerm term) {
            this.newState = newState;
            this.term = term;
        }

        public SolverState getNewState() {
            return newState;
        }

        public ITerm getTerm() {
            return term;
        }
    }
}
