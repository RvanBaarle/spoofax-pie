package mb.statix.completions;

import com.google.common.collect.ImmutableMap;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.sequences.Seq;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.PlaceholderVarMap;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.strategies.AbstractStrategy;
import mb.strategies.AbstractStrategy1;
import mb.strategies.AbstractStrategy2;
import mb.strategies.DebugStrategy;
import mb.strategies.Strategies;
import mb.strategies.Strategy;
import mb.strategies.Strategy1;
import mb.strategies.Strategy2;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static mb.statix.search.CollectionExt.containsAny;
import static mb.statix.strategies.SearchStrategies.*;
import static mb.strategies.Strategies.*;
import static mb.strategies.Strategy1.define;
import static mb.strategies.Strategy2.define;

@SuppressWarnings("Convert2Diamond") public final class CompleteStrategies {
    private CompleteStrategies() {
    }

    private static final Strategy2<SolverContext, ITermVar, PlaceholderVarMap, SolverState, IStrategoTerm> getCompletionProposals
        = define("getCompletionProposals", (v, m) ->
            seq(complete(v, Collections.emptySet()))
            .$(filterPlaceholders(v))
            .$(filterLiteralPlaceholders(v))
            .$(toTerm(v))
            .$(replaceVariablesByPlaceholders(v, m))
            .$(toStrategoTerm())
            .$()
        );

    public static Strategy<SolverContext, SolverState, IStrategoTerm> getCompletionProposals(ITermVar v, PlaceholderVarMap placeholderVarMap) {
        return getCompletionProposals.apply(v, placeholderVarMap);
    }

    /**
     * Completes the given placeholder.
     */
    private static final Strategy2<SolverContext, ITermVar, Set<String>, SolverState, SolverState> complete
        = define("complete", (v, visitedInjections) -> debugState(v,
            seq(expandAllPredicates(v))
            .$(expandAllInjections(v, visitedInjections))
            .$(expandAllQueries(v))
            .$(expandDeterministic(v))
            .$())
        );

    public static Strategy<SolverContext, SolverState, SolverState> complete(ITermVar v, Set<String> visitedInjections) {
        return complete.apply(v, visitedInjections);
    }

    /**
     * Expand predicate constraints that contain the specified variable.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandAllPredicates
        = define("expandAllPredicates", v ->
        // We need to repeat this, because there might be more than one constraint that limit(1, select..) might select.
        // For example, (and this happened), the first selected constraint may be subtypeOf(), which when completed
        // doesn't result in any additional syntax. We first need to expand the next constraint, typeOfType()
        // to get actually a useful result.
        // An example where this happens is in this program, on the $Type placeholder:
        //   let function $ID(): $Type = $Exp in 3 end
        debugState(v, repeat(seq(limit(1, selectConstraints(CUser.class, (constraint, state) -> containsVar(v, constraint, state))))
            // Expand the focussed rule
            .$(expandPredicateConstraint(v))
            // Perform inference and remove states that have errors
            .$(assertValid(v))
            .$())));

    public static Strategy<SolverContext, SolverState, SolverState> expandAllPredicates(ITermVar v) {
        return expandAllPredicates.apply(v);
    }


    /**
     * Expand predicate constraints on injections that contain the specified variable.
     */
    private static final Strategy2<SolverContext, ITermVar, Set<String>, SolverState, SolverState> expandAllInjections
        = define("expandAllInjections", (v, visitedInjections) -> {
        return
            debugState(v, seq(fixSet(try_(     // Fixset-try because we need to expand injections one-by-one
                expandInjection(visitedInjections, v)
            )))
            // Perform inference and remove states that have errors
            .$(assertValid(v))
            .$());
    });

    public static Strategy<SolverContext, SolverState, SolverState> expandAllInjections(ITermVar v, Set<String> visitedInjections) {
        return expandAllInjections.apply(v, visitedInjections);
    }

    /**
     * Expand the injection, if any.
     */
    public static class ExpandInjection extends AbstractStrategy2<SolverContext, Set<String>, ITermVar, SolverState, SolverState> {
        public final Predicate<ITerm> isInjPredicate;

        public ExpandInjection(Predicate<ITerm> isInjPredicate) {
            this.isInjPredicate = isInjPredicate;
        }

        @Override public Seq<SolverState> eval(SolverContext ctx, Set<String> visitedInjections, ITermVar v, SolverState state) {
            // Project the term
            final ITerm term = state.project(v);

            // Ensure it is an injection application with one argument which is a variable
            if (!(term instanceof IApplTerm)) return Seq.empty();
            final IApplTerm injTerm = (IApplTerm)term;
            if (injTerm.getArgs().size() != 1) return Seq.empty();
            final ITerm injArg = injTerm.getArgs().get(0);
            if (!(injArg instanceof ITermVar) || !isInjPredicate.test(term)) return Seq.empty();

            // Ensure the injection was not already visited
            final String injName = injTerm.getOp();
            final ITermVar injArgVar = (ITermVar)injArg;
            if(visitedInjections.contains(injName)) return Seq.empty();
            final Set<String> newVisitedInjections = setWithElement(visitedInjections, injName);

            // Complete the injection
            return complete(injArgVar, newVisitedInjections).eval(ctx, state);
        }

        @Override public String getName() { return "expandInjection"; }
    }

    // FIXME: Dirty workaround; this is initialized somewhere else
    static Strategy2<SolverContext, Set<String>, ITermVar, SolverState, SolverState> expandInjection;
    public static Strategy<SolverContext, SolverState, SolverState> expandInjection(Set<String> visitedInjections, ITermVar v) {
        return expandInjection.apply(visitedInjections, v);
    }

    /**
     * Expand all query constraints that contain the specified variable.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandAllQueries
        = define("expandAllQueries", v -> debugState(v, fixSet(
            if_(
                limit(1, selectConstraints(CResolveQuery.class)),
                seq(debugCResolveQuery(v, expandQueryConstraint()))
                .$(assertValid(v))
                .$(),
                id()
            )
        )));

    public static Strategy<SolverContext, SolverState, SolverState> expandAllQueries(ITermVar v) {
        return expandAllQueries.apply(v);
    }



    /**
     * Expand anything deterministically.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandDeterministic
        = define("expandDeterministic", v -> debugState(v,
        fixSet(try_(seq(printSolverState("EXPAND STATE", selectConstraints(CUser.class, (constraint, state) -> {
                final io.usethesource.capsule.Set.Immutable<ITermVar> innerVars = state.project(v).getVars();
                return containsAnyVar(innerVars, constraint, state);
            })))
            .$(debugCUser(v, single(
                seq(expandPredicateConstraint(v))
                // Perform inference and remove states that have errors
                .$(assertValid(v))
                // Remove naked placeholders
                .$(filterPlaceholders(v))
                .$()
            )))
        .$()))));

    public static Strategy<SolverContext, SolverState, SolverState> expandDeterministic(ITermVar v) {
        return expandDeterministic.apply(v);
    }

//    /**
//     * Remove literals and naked placeholders.
//     */
//    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> filterLiteralsAndPlaceholders
//        = define("filterLiteralsAndPlaceholders", v -> Strategies.assertThat(s -> !StrategoPlaceholders.isPlaceholder(s.project(v))));
//
//    public static Strategy<SolverContext, SolverState, SolverState> filterLiteralsAndPlaceholders(ITermVar v) {
//        return filterLiteralsAndPlaceholders.apply(v);
//    }

    /**
     * Remove naked placeholders.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> filterPlaceholders
        = define("filterPlaceholders", v -> Strategies.assertThat(s -> {
            final ITerm t = s.project(v);
            return !StrategoPlaceholders.isPlaceholder(t) && !(t instanceof ITermVar);
        }));

    public static Strategy<SolverContext, SolverState, SolverState> filterPlaceholders(ITermVar v) {
        return filterPlaceholders.apply(v);
    }

    /**
     * Remove placeholders for literals.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> filterLiteralPlaceholders
        = define("filterLiteralPlaceholders", v -> Strategies.assertThat(s -> {
        final ITerm t = s.project(v);
        return !StrategoPlaceholders.containsLiteralVar(t);
    }));

    public static Strategy<SolverContext, SolverState, SolverState> filterLiteralPlaceholders(ITermVar v) {
        return filterLiteralPlaceholders.apply(v);
    }

    /**
     * Perform inference, and reject the resulting state if it has errors.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> assertValid
        = define("assertValid", v -> debugState(v, printSolverState("BEFORE INFER",
        seq(infer())
        .$(printSolverState("AFTER INFER", id()))
        // Remove states that have errors
        .$(assertThat(s -> {
            boolean valid = !s.hasErrors();
            if(!valid && DebugStrategy.debug) {
                System.out.println("REJECTED: " + s.project(v));
            }
            return valid;
        }))
        // Delay stuck queries
        .$(delayStuckQueries())
        .$(printSolverState("AFTER DELAY", id()))
        .$())));

    public static Strategy<SolverContext, SolverState, SolverState> assertValid(ITermVar v) {
        return assertValid.apply(v);
    }

    /**
     * Projects the focussed term variable to a tuple of the state and its projected term.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, ITerm> toTerm
        = define("toTerm", v -> map(s -> s.project(v)));

    public static Strategy<SolverContext, SolverState, ITerm> toTerm(ITermVar v) {
        return toTerm.apply(v);
    }

    /**
     * Replaces the variables in the term by placeholders.
     */
    private static final Strategy2<SolverContext, ITermVar, PlaceholderVarMap, ITerm, ITerm> replaceVariablesByPlaceholders
        = define("replaceVariablesByPlaceholders", (v, m) -> map(t -> StrategoPlaceholders.replaceVariablesByPlaceholders(t, m)));

    public static Strategy<SolverContext, ITerm, ITerm> replaceVariablesByPlaceholders(ITermVar v, PlaceholderVarMap placeholderVarMap) {
        return replaceVariablesByPlaceholders.apply(v, placeholderVarMap);
    }

    /**
     * Replaces the variables in the term by placeholders.
     */
    private static final Strategy<SolverContext, ITerm, IStrategoTerm> toStrategoTerm
        = new AbstractStrategy<SolverContext, ITerm, IStrategoTerm>() {
            @Override
            public Seq<IStrategoTerm> eval(SolverContext solverContext, ITerm input) {
                // TODO: Make lazy
                return Seq.of(solverContext.getStrategoTerms().toStratego(input));
            }

            @Override
            public String getName() {
                return "toStrategoTerm";
            }
        };

    public static Strategy<SolverContext, ITerm, IStrategoTerm> toStrategoTerm() {
        return toStrategoTerm;
    }



    private static boolean containsVar(ITermVar var, CUser constraint, SolverState state) {
        return containsAnyVar(Collections.singletonList(var), constraint, state);
    }

    private static boolean containsAnyVar(Collection<ITermVar> vars, CUser constraint, SolverState state) {
        @Nullable final ImmutableMap<ITermVar, ITermVar> existentials = state.getExistentials();
        final ArrayList<ITermVar> projectedVars = new ArrayList<>(vars.size());
        if(existentials != null) {
            for(ITermVar var : vars) {
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
        for(ITerm arg : constraint.args()) {
            final io.usethesource.capsule.Set.Immutable<ITermVar> constraintVars = state.getState().unifier().getVars(arg);
            final boolean match = !constraintVars.isEmpty() ? containsAny(constraintVars, projectedVars) : projectedVars.contains(arg);
            if(match) return true;
        }
        return false;
    }

    private static final Strategy2<SolverContext, ITermVar, Strategy<SolverContext, SolverState, SolverState>, SolverState, SolverState> debugState
        = Strategy2.define("debugState", (v, s) -> Strategies.debug(it -> it.project(v).toString(), it -> it.project(v).toString(), s));
    public static Strategy<SolverContext, SolverState, SolverState> debugState(ITermVar v, Strategy<SolverContext, SolverState, SolverState> s) {
        return debugState.apply(v, s);
    }

    private static final Strategy2<SolverContext, ITermVar, Strategy<SolverContext, SelectedConstraintSolverState<CUser>, SolverState>, SelectedConstraintSolverState<CUser>, SolverState> debugCUser
        = Strategy2.define("debugCUser", (v, s) -> Strategies.debug(it -> it.getSelected().toString(), it -> it.project(v).toString(), s));
    public static Strategy<SolverContext, SelectedConstraintSolverState<CUser>, SolverState> debugCUser(ITermVar v, Strategy<SolverContext, SelectedConstraintSolverState<CUser>, SolverState> s) {
        return debugCUser.apply(v, s);
    }

    private static final Strategy2<SolverContext, ITermVar, Strategy<SolverContext, SelectedConstraintSolverState<CResolveQuery>, SolverState>, SelectedConstraintSolverState<CResolveQuery>, SolverState> debugCResolveQuery
        = Strategy2.define("debugCResolveQuery", (v, s) -> Strategies.debug(it -> it.getSelected().toString(), it -> it.project(v).toString(), s));
    public static Strategy<SolverContext, SelectedConstraintSolverState<CResolveQuery>, SolverState> debugCResolveQuery(ITermVar v, Strategy<SolverContext, SelectedConstraintSolverState<CResolveQuery>, SolverState> s) {
        return debugCResolveQuery.apply(v, s);
    }

    public static <SolverState, O> Strategy<SolverContext, SolverState, O> printSolverState(String prefix, Strategy<SolverContext, SolverState, O> s) {
        return new AbstractStrategy1<SolverContext, Strategy<SolverContext, SolverState, O>, SolverState, O>() {
            @Override
            public Seq<O> eval(SolverContext ctx, Strategy<SolverContext, SolverState, O> s, SolverState input) {
                if (DebugStrategy.debug) {
                    System.out.println(prefix + ": " + input);
                }
                return s.eval(ctx, input);
            }

            @Override
            public String getName() {
                return "printSolverState";
            }
        }.apply(s);
    }

    /**
     * Creates a new set of the given set and thr given element.
     *
     * @param set     the set
     * @param element the element to add
     * @param <T>     the type of elements
     * @return the new set, which is a union of the original set and the singleton set of the given element
     */
    private static <T> Set<T> setWithElement(Set<T> set, T element) {
        // TODO: Use a set more optimized for immutable operations
        final HashSet<T> newSet = new HashSet<>(set);
        newSet.add(element);
        return Collections.unmodifiableSet(newSet);
    }
}