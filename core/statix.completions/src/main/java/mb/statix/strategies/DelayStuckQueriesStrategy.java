package mb.statix.strategies;

import com.google.common.collect.Maps;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.sequences.Computation;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.CEqual;
import mb.statix.constraints.CResolveQuery;
import mb.statix.generator.scopegraph.DataWF;
import mb.statix.generator.scopegraph.NameResolution;
import mb.statix.generator.strategy.ResolveDataWF;
import mb.statix.scopegraph.reference.EdgeOrData;
import mb.statix.scopegraph.reference.IncompleteException;
import mb.statix.scopegraph.reference.LabelOrder;
import mb.statix.scopegraph.reference.LabelWF;
import mb.statix.scopegraph.reference.ResolutionException;
import mb.statix.scopegraph.terms.Scope;
import mb.statix.solver.CriticalEdge;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.statix.solver.IState;
import mb.statix.solver.completeness.ICompleteness;
import mb.statix.solver.query.RegExpLabelWF;
import mb.statix.solver.query.RelationLabelOrder;
import mb.statix.spec.Spec;
import mb.strategies.AbstractStrategy;
import mb.strategies.Strategy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.metaborg.util.functions.Predicate2;

import java.util.Optional;


/**
 * Delays stuck queries.
 */
public final class DelayStuckQueriesStrategy extends AbstractStrategy<SolverContext, SolverState, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final DelayStuckQueriesStrategy instance = new DelayStuckQueriesStrategy();
    @SuppressWarnings("unchecked")
    public static DelayStuckQueriesStrategy getInstance() { return instance; }

    private DelayStuckQueriesStrategy() {}

    @Override
    public String getName() {
        return "delayStuckQueries";
    }

    @Override
    protected Seq<SolverState> innerEval(SolverContext ctx, SolverState input) {
        final IState.Immutable state = input.getState();
        final ICompleteness.Immutable completeness = input.getCompleteness();

        final java.util.Map<IConstraint, Delay> delays = Maps.newHashMap();
        input.getConstraints().stream().filter(c -> c instanceof CResolveQuery).map(c -> (CResolveQuery) c).forEach(q -> checkDelay(ctx.getSpec(), q, state, completeness).ifPresent(d -> {
            delays.put(q, d);
        }));

        return Seq.of(input.delay(delays.entrySet()));
    }

    private Optional<Delay> checkDelay(Spec spec, CResolveQuery query, IState.Immutable state,
                                       ICompleteness.Immutable completeness) {
        final IUniDisunifier unifier = state.unifier();

        if(!unifier.isGround(query.scopeTerm())) {
            return Optional.of(Delay.ofVars(unifier.getVars(query.scopeTerm())));
        }
        @Nullable final Scope scope = Scope.matcher().match(query.scopeTerm(), unifier).orElse(null);
        if(scope == null) {
            return Optional.empty();
        }

        @Nullable final Boolean isAlways;
        try {
            isAlways = query.min().getDataEquiv().isAlways(spec).orElse(null);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(isAlways == null) {
            return Optional.empty();
        }

        final LabelWF<ITerm> labelWF = RegExpLabelWF.of(query.filter().getLabelWF());
        final LabelOrder<ITerm> labelOrd = new RelationLabelOrder(query.min().getLabelOrder());
        final DataWF<ITerm, CEqual> dataWF = new ResolveDataWF(state, completeness, query.filter().getDataWF(), query);
        final Predicate2<Scope, EdgeOrData<ITerm>> isComplete =
            (s, l) -> completeness.isComplete(s, l, state.unifier());

        // @formatter:off
        final NameResolution<Scope, ITerm, ITerm, CEqual> nameResolution = new NameResolution<>(
            spec,
            state.scopeGraph(),
            spec.allLabels(),
            labelWF, labelOrd,
            dataWF, isAlways, isComplete);
        // @formatter:on

        try {
            nameResolution.resolve(scope, () -> false);
        } catch(IncompleteException e) {
            return Optional.of(Delay.ofCriticalEdge(CriticalEdge.of(e.scope(), e.label())));
        } catch(ResolutionException e) {
            throw new RuntimeException("Unexpected resolution exception.", e);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

}
