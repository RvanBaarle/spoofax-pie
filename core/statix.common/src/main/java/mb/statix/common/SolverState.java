package mb.statix.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.usethesource.capsule.Map;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.nabl2.terms.unification.Unifiers;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.nabl2.util.CapsuleUtil;
import mb.nabl2.util.Tuple2;
import mb.statix.constraints.CConj;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CExists;
import mb.statix.constraints.CInequal;
import mb.statix.constraints.messages.IMessage;
import mb.statix.constraints.messages.MessageKind;
import mb.statix.solver.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.Completeness;
import mb.statix.solver.completeness.CompletenessUtil;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.persistent.SolverResult;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Spec;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.metaborg.util.functions.Function2;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.metaborg.util.optionals.Optionals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static mb.nabl2.terms.build.TermBuild.B;


/**
 * The state of the solver.
 */
public final class SolverState {

    private final static ILogger log = LoggerUtils.logger(SolverState.class);

    /**
     * Creates a new {@link SolverState} from the given specification, solver state, and constraints.
     *
     * @param spec the Statix specification
     * @param state the solver state
     * @param constraints the constraints
     * @return the resulting search state
     */
    public static SolverState of(Spec spec, IState.Immutable state, Iterable<? extends IConstraint> constraints) {
        final ICompleteness.Transient completeness = Completeness.Transient.of();
        completeness.addAll(constraints, spec, state.unifier());

        return new SolverState(state, Map.Immutable.of(), CapsuleUtil.toSet(constraints), Map.Immutable.of(),
            null, completeness.freeze());
    }

    /**
     * Creates a new {@link SolverState} from the given solver result.
     *
     * @param result the result of inference by the solver
     * @param existentials
     * @return the resulting search state
     */
    public static SolverState fromSolverResult(SolverResult result, @Nullable ImmutableMap<ITermVar, ITermVar> existentials) {
        final Set.Transient<IConstraint> constraints = Set.Transient.of();
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        result.delays().forEach((c, d) -> {
            if(d.criticalEdges().isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });

        final ImmutableMap<ITermVar, ITermVar> newExistentials =
            existentials == null ? result.existentials() : existentials;
        return new SolverState(result.state(), CapsuleUtil.toMap(result.messages()), constraints.freeze(), delays.freeze(), newExistentials,
            result.completeness());
    }

    private final IState.Immutable state;
    private final Set.Immutable<IConstraint> constraints;
    private final Map.Immutable<IConstraint, Delay> delays;
    @Nullable private final ImmutableMap<ITermVar, ITermVar> existentials;
    private final ICompleteness.Immutable completeness;
    private final Map.Immutable<IConstraint, IMessage> messages;

    protected SolverState(IState.Immutable state,
                          Map.Immutable<IConstraint, IMessage> messages,
                          Set.Immutable<IConstraint> constraints,
                          Map.Immutable<IConstraint, Delay> delays,
                          @Nullable ImmutableMap<ITermVar, ITermVar> existentials,
                          ICompleteness.Immutable completeness) {
        this.state = state;
        this.messages = messages;
        this.constraints = constraints;
        this.delays = delays;
        this.existentials = existentials;
        this.completeness = completeness;
    }

    /** Gets the solver state. */
    public IState.Immutable getState() {
        return state;
    }

    /** Gets the messages. */
    public Map.Immutable<IConstraint, IMessage> getMessages() {
        return this.messages;
    }

    /** Determines whether any of the messages in this state are error messages. */
    public boolean hasErrors() {
        return this.messages.values().stream().anyMatch(m -> m.kind().equals(MessageKind.ERROR));
    }

    /** The constraints left to solve. */
    public Set.Immutable<IConstraint> getConstraints() {
        return constraints;
    }

    /** The constraints that have been delayed due to critical edges. */
    public Map.Immutable<IConstraint, Delay> getDelays() {
        return delays;
    }

    /**
     * The variables that have been existentially quantified in the most top-level constraint;
     * or {@code null} when no constraints have existentially quantified any variables (yet).
     *
     * This is used to be able to find the value assigned to the top-most quantified variables.
     */
    @Nullable public ImmutableMap<ITermVar, ITermVar> getExistentials() {
        return this.existentials;
    }

    public ICompleteness.Immutable getCompleteness() {
        return completeness;
    }

    /**
     * Updates the existentials to be tracked in the state.
     *
     * @param existentials the existentials to track
     * @return the new solver state
     */
    public SolverState withExistentials(Iterable<ITermVar> existentials) {
        // We wrap all constraints in a conjunction,
        // and wrap the result in an existential constraint.
        SolverState newState = foldToSingleConstraint();
        assert newState.constraints.size() <= 1;
        if (newState.constraints.isEmpty()) {
            // No constraints, so what can you do? ¯\_(ツ)_/¯
            return this;
        }
        final IConstraint constraint = this.constraints.iterator().next();
        final IConstraint newConstraint = new CExists(existentials, constraint);
        return new SolverState(this.state, this.messages, Set.Immutable.of(newConstraint), this.delays, null, this.completeness);
    }

    /**
     * Folds the constraints into a single conjunction.
     *
     * @return the new solver state with a single conjunction
     */
    public SolverState foldToSingleConstraint() {
        if (this.constraints.size() <= 1) return this;
        Iterator<IConstraint> iterator = this.constraints.iterator();
        // We wrap all constraints in a conjunction.
        IConstraint newConstraint = iterator.next();
        while(iterator.hasNext()) {
            newConstraint = new CConj(newConstraint, iterator.next());
        }
        return new SolverState(this.state, this.messages, Set.Immutable.of(newConstraint), this.delays, null, this.completeness);
    }

    /**
     * Pre-computes the critical edges.
     *
     * @param spec the spec
     * @return the new solver state with pre-computed critical edges
     */
    public SolverState precomputeCriticalEdges(Spec spec) {
        SolverState newState = foldToSingleConstraint();
        assert newState.constraints.size() <= 1;
        if (newState.constraints.isEmpty()) {
            // No constraints, so what can you do? ¯\_(ツ)_/¯
            return this;
        }
        final IConstraint constraint = newState.constraints.iterator().next();
        final Tuple2<IConstraint, ICompleteness.Immutable> result =
            CompletenessUtil.precomputeCriticalEdges(constraint, spec.scopeExtensions());
        final IConstraint newConstraint = result._1();
        ICompleteness.Transient completeness = this.completeness.melt();
        completeness.addAll(result._2(), state.unifier());
        return new SolverState(this.state, this.messages, Set.Immutable.of(newConstraint), this.delays, null, completeness.freeze());
    }

    /**
     * Updates this search state with the specified {@link ApplyResult} and returns the new state.
     *
     * @param result the {@link ApplyResult}
     * @param focus the focus constraint
     * @return the updated search state
     */
    public SolverState withApplyResult(SolverContext ctx, IConstraint focus, ApplyResult result) {
        final IConstraint applyConstraint = result.body();
        final IState.Immutable applyState = this.state;
        final IUniDisunifier.Immutable applyUnifier = applyState.unifier();

        // Update constraints
        final Set.Transient<IConstraint> constraints = this.getConstraints().asTransient();
        constraints.__insert(applyConstraint);
        constraints.__remove(focus);

        // Update completeness
        final ICompleteness.Transient completeness = this.getCompleteness().melt();
        completeness.add(applyConstraint, ctx.getSpec(), applyUnifier);
        java.util.Set<CriticalEdge> removedEdges = completeness.remove(focus, ctx.getSpec(), applyUnifier);

        // Update delays
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        this.getDelays().forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });

        return new SolverState(applyState, messages, constraints.freeze(), delays.freeze(), existentials, completeness.freeze());
    }

    /**
     * Update the constraints, keeping completeness and delayed constraints in sync.
     *
     * This method assumes that no constraints appear in both add and remove, or it will be incorrect!
     *
     * @param add the constraints to add
     * @param remove the constraints to remove
     * @return the new search state
     */
    public SolverState updateConstraints(Spec spec, Iterable<IConstraint> add, Iterable<IConstraint> remove) {
        final ICompleteness.Transient completeness = this.completeness.melt();
        final Set.Transient<IConstraint> constraints = this.constraints.asTransient();
        final java.util.Set<CriticalEdge> removedEdges = Sets.newHashSet();
        add.forEach(c -> {
            if(constraints.__insert(c)) {
                completeness.add(c, spec, state.unifier());
            }
        });
        remove.forEach(c -> {
            if(constraints.__remove(c)) {
                removedEdges.addAll(completeness.remove(c, spec, state.unifier()));
            }
        });
        final Map.Transient<IConstraint, Delay> delays = Map.Transient.of();
        this.delays.forEach((c, d) -> {
            if(!Sets.intersection(d.criticalEdges(), removedEdges).isEmpty()) {
                constraints.__insert(c);
            } else {
                delays.__put(c, d);
            }
        });
        return new SolverState(state, messages, constraints.freeze(), delays.freeze(), existentials, completeness.freeze());
    }

    public SolverState delay(Iterable<? extends java.util.Map.Entry<IConstraint, Delay>> delay) {
        final Set.Transient<IConstraint> constraints = this.constraints.asTransient();
        final Map.Transient<IConstraint, Delay> delays = this.delays.asTransient();
        delay.forEach(entry -> {
            if(constraints.__remove(entry.getKey())) {
                delays.__put(entry.getKey(), entry.getValue());
            } else {
                log.warn("delayed constraint not in constraint set: {}", entry.getKey());
            }
        });
        return new SolverState(state, messages, constraints.freeze(), delays.freeze(), existentials, completeness);
    }

    /**
     * Projects the variable to a value through the unifier.
     *
     * @param var the variable to project
     * @return the fully instantiated value associated with the variable; or itself when not found
     */
    public ITerm project(ITermVar var) {
        // val var3 = existentials?.get(var) ?: var
        @Nullable final ImmutableMap<ITermVar, ITermVar> existentials = getExistentials();
        @Nullable final ITermVar var2 = (existentials != null ? existentials.get(var) : null);
        final ITermVar var3 = (var2 != null ? var2 : var);

        return getState().unifier().findRecursive(var3);
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        SolverState that = (SolverState)o;
        return state.equals(that.state) &&
            constraints.equals(that.constraints) &&
            delays.equals(that.delays) &&
            Objects.equals(existentials, that.existentials) &&
            completeness.equals(that.completeness) &&
            messages.equals(that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, constraints, delays, existentials, completeness, messages);
    }

    @Override public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        try {
            writer.println("SolverState:");
            write(writer, (t, u) -> new UnifierFormatter(u, /* Increased from 2 */ 8).format(t));
        } catch (IOException e) {
            // This can never happen.
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public void write(PrintWriter writer, Function2<ITerm, IUniDisunifier, String> prettyprinter) throws IOException {
        final IUniDisunifier unifier = state.unifier();
        if (existentials != null) {
            writer.println("| vars:");
            for (Map.Entry<ITermVar, ITermVar> existential : existentials.entrySet()) {
                String var = prettyprinter.apply(existential.getKey(), Unifiers.Immutable.of());
                String term = prettyprinter.apply(existential.getValue(), unifier);
                writer.println("|   " + var + " : " + term);
            }
        } else {
            writer.println("| vars: <null>");
        }
        writer.println("| unifier: " + state.unifier().toString());
        writer.println("| completeness: " + completeness.toString());
        writer.println("| constraints:");
        for (IConstraint c : constraints) {
            writer.println("|   " + c.toString(t -> prettyprinter.apply(t, unifier)));
        }
        writer.println("| delays:");
        for (java.util.Map.Entry<IConstraint, Delay> e : delays.entrySet()) {
            writer.println("|   " + e.getValue() + " : " + e.getKey().toString(t -> prettyprinter.apply(t, unifier)));
        }
        writer.println("| messages:");
        for (java.util.Map.Entry<IConstraint, IMessage> e : messages.entrySet()) {
            writer.println("|   - " + e.getValue().toString(ITerm::toString));
            writer.println("|     " + e.getKey().toString(t -> prettyprinter.apply(t, unifier)));
        }
    }
}
