package mb.statix.completions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;
import mb.data.Tuple3;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.constraints.CResolveQuery;
import mb.statix.constraints.CUser;
import mb.strategies.Strategies;
import mb.strategies.Strategy;
import mb.strategies.Strategy1;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static mb.statix.search.CollectionExt.containsAny;
import static mb.statix.strategies.SearchStrategies.*;
import static mb.strategies.Strategies.*;

@SuppressWarnings("Convert2Diamond") public final class CompleteStrategies {
    private CompleteStrategies() {
    }


    /**
     * Completes the given placeholder.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> complete
        = Strategy1.define("complete", v ->
        seq(expandAllRules(v))
//        .$(expandAllQueries(v))
//        .$(expandDeterministic(v))
//        .$(filterLiterals(v))
        .$());

    public static Strategy<SolverContext, SolverState, SolverState> complete(ITermVar v) {
        return complete.apply(v);
    }


    /**
     * Perform inference, and reject the resulting state if it has errors.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> assertValid
        = Strategy1.define("assertValid", v -> seq(infer())
            // Remove states that have errors
            //.$(assertThat(s -> !s.hasErrors()))
            .$(assertThat(s -> {
                boolean valid = !s.hasErrors();
                if(!valid) {
                    System.out.println("REJECTED: " + s.project(v));
                }
                return valid;
            }))
            // Delay stuck queries
            .$(delayStuckQueries())
            .$());

    public static Strategy<SolverContext, SolverState, SolverState> assertValid(ITermVar v) {
        return assertValid.apply(v);
    }


    /**
     * If the project term is an injection (with a single argument), this unwraps the injection
     * and returns a triple of (injection constructor name, injection argument, solver state).
     * Otherwise, this strategy fails.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, Tuple3<String, ITermVar, SolverState>> unwrapInjection
        = Strategy1.define("unwrapInjection", (ctx, v, s) -> {
            final ITerm term = s.project(v);
            if(!StrategoPlaceholders.isInjectionConstructor(term)) {
                System.out.println("Injection?  NO: " + term);
                return Seq.empty();
            } else {
                System.out.println("Injection? YES: " + term);
            }
            final IApplTerm appl = (IApplTerm)term;
            final String sortName = StrategoPlaceholders.getInjectionSortName(appl);
            final ITermVar injArg = StrategoPlaceholders.getInjectionArgument(appl);
            System.out.println("(\"" + sortName + "\", " + injArg + ")");
            return Seq.of(Tuple3.of(sortName, injArg, s));
        });

    public static Strategy<SolverContext, SolverState, Tuple3<String, ITermVar, SolverState>> unwrapInjection(ITermVar v) {
        return unwrapInjection.apply(v);
    }

    /**
     * Expands an injection.
     */
    private static final Strategy1<SolverContext, Set<String>, Tuple3<String, ITermVar, SolverState>, SolverState> expandInjection
        = Strategy1.define("expandInjection", (ctx, visitedInjections, tuple) -> {
            final String injName = tuple.getComponent1();
            final ITermVar v = tuple.getComponent2();
            final SolverState s = tuple.getComponent3();
            // Check that we are not expanding a previously expanded injection
            final ITerm term = s.project(v);
            if(visitedInjections.contains(injName)) {
                System.out.println("Injection visited? YES: " + term);
                return Seq.empty();
            } else {
                System.out.println("Injection visited?  NO: " + term);
            }
            // TODO: Use visited list
            final Set<String> newVisitedInjections = setWithElement(visitedInjections, injName);
            // Expand the given variable in the state
            Seq<SolverState> apply = complete(v).eval(ctx, s);
            System.out.println("Injection " + term + " expanded to: ");
            for(SolverState s1 : apply.buffer().asIterable()) {
                System.out.println("- " + s1.project(v));
            }
            return apply;
        });

    public static Strategy<SolverContext, Tuple3<String, ITermVar, SolverState>, SolverState> expandInjection(Set<String> visitedInjections) {
        return expandInjection.apply(visitedInjections);
    }

    /**
     * Expand predicate constraints on injections that contain the specified variable.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandAllInjections
        = Strategy1.define("expandAllInjections", (ctx, v, input) -> {
            AtomicReference<Set<String>> visitedInjections = new AtomicReference<>(Collections.emptySet());
            return //seq(Strategies.<SolverContext, SolverState>accept(o -> visitedInjections.set(Collections.emptySet())))
                seq(fixSet(try_(     // Fixset-try because we need to expand injections one-by-one
                    seq(unwrapInjection(v))
                    .$(expandInjection(visitedInjections.get()))
                    .$()
                )))
                .$()
                .eval(ctx, input);
        });

    public static Strategy<SolverContext, SolverState, SolverState> expandAllInjections(ITermVar v) {
        return expandAllInjections.apply(v);
    }

    /**
     * Expand predicate constraints that contain the specified variable.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandAllRules
        = Strategy1.define("expandAllRules", v -> seq(limit(1, focusConstraint(CUser.class, (constraint, state) -> containsVar(v, constraint, state))))
            // Expand the focussed rule
            .$(expandPredicateConstraint())
            // Recursively expand injections
            .$(expandAllInjections(v))
            // Perform inference and remove states that have errors
            .$(assertValid(v))
            .$());

    public static Strategy<SolverContext, SolverState, SolverState> expandAllRules(ITermVar v) {
        return expandAllRules.apply(v);
    }

    /**
     * Expand all query constraints that contain the specified variable.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandAllQueries
        = Strategy1.define("expandAllQueries", v -> fixSet(try_(   // Expand each query constraint until there are none left
            seq(limit(1, focusConstraint(CResolveQuery.class)))
                // Expand the query into its results
                .$(expandQueryConstraint())
                // Perform inference and remove states that have errors
                .$(assertValid(v))
                .$()
        )));

    public static Strategy<SolverContext, SolverState, SolverState> expandAllQueries(ITermVar v) {
        return expandAllQueries.apply(v);
    }

//    public static <CTX, T, R> Strategy<CTX, T, R> defStrategy(String name, Strategy<CTX, T, R> body) {
//
//        return new Strategy<CTX, T, R>() {
//            @Override public String getName() { return name; }
//
//            @Override public Seq<R> apply(CTX ctx, T input) {
//                return body.apply(ctx, input);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString();
//            }
//        };
//    }

    /**
     * Expand anything deterministically.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> expandDeterministic
        = Strategy1.define("expandDeterministic", v -> seq(focusConstraint(CUser.class, (constraint, state) -> {
            final Multiset<ITermVar> innerVars = state.project(v).getVars();
            return containsAnyVar(innerVars, constraint, state);
        }))
            .$(single(
                seq(expandPredicateConstraint())
                    // Perform inference and remove states that have errors
                    .$(assertValid(v))
                    .$()
            ))
            .$());

    public static Strategy<SolverContext, SolverState, SolverState> expandDeterministic(ITermVar v) {
        return expandDeterministic.apply(v);
    }

    /**
     * Remove literals and naked placeholders.
     */
    private static final Strategy1<SolverContext, ITermVar, SolverState, SolverState> filterLiterals
        = Strategy1.define("filterLiterals", (ctx, v, input) -> Strategies.<SolverContext, SolverState>assertThat(s -> !StrategoPlaceholders.isPlaceholder(s.project(v)))
            .eval(ctx, input));

    public static Strategy<SolverContext, SolverState, SolverState> filterLiterals(ITermVar v) {
        return filterLiterals.apply(v);
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
