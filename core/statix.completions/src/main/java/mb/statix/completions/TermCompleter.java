package mb.statix.completions;

import com.google.common.collect.ImmutableMap;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.CUser;
import mb.strategies.Strategy;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static mb.statix.search.CollectionExt.containsAny;

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
            return completeNodes(ctx, state, placeholderVar).map(s -> new CompletionSolverProposal(s, s.project(placeholderVar))).toList().eval();
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
    public Seq<SolverState> completeNodes(SolverContext ctx, SolverState state, ITermVar placeholderVar) throws InterruptedException {
        Strategy<SolverContext, SolverState, SolverState> strategy = CompleteStrategies.complete(placeholderVar);
        System.out.println("COMPLETING: " + strategy);
        return strategy.eval(ctx, state);
//        return new CompleteStrategy(placeholderVar).apply(ctx, state);
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
