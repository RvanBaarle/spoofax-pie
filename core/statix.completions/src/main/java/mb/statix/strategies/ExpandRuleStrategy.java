package mb.statix.strategies;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.nabl2.util.Tuple2;
import mb.nabl2.util.VoidException;
import mb.sequences.Seq;
import mb.statix.common.SelectedConstraintSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.CUser;
import mb.statix.solver.IConstraint;
import mb.statix.spec.ApplyMode;
import mb.statix.spec.ApplyResult;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;
import mb.strategies.AbstractStrategy;
import mb.strategies.AbstractStrategy1;
import mb.strategies.DebugStrategy;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy extends AbstractStrategy1<SolverContext, ITermVar, SelectedConstraintSolverState<CUser>, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final ExpandRuleStrategy instance = new ExpandRuleStrategy();
    @SuppressWarnings("unchecked")
    public static ExpandRuleStrategy getInstance() { return (ExpandRuleStrategy)instance; }

    private ExpandRuleStrategy() {}

    @Override
    public String getName() {
        return "expandRule";
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches") @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "focus";
            default: return super.getParamName(index);
        }
    }

    @Override
    protected Seq<SolverState> innerEval(SolverContext ctx, @Nullable ITermVar focus, SelectedConstraintSolverState<CUser> state) {
        CUser selected = state.getSelected();
//        if (DebugStrategy.debug) {
//            System.out.println("EXPAND RULE: " + selected);
//        }

        final ImmutableSet<Rule> rules = ctx.getSpec().rules().getOrderIndependentRules(selected.name());
        SolverState searchState = state.getInnerState();
//        Stream<SolverState> output = applyAllLazy(searchState.getState().unifier(), rules, selected.args(), selected, ApplyMode.RELAXED)//.stream()
        List<SolverState> output = RuleUtil.applyAll(searchState.getState().unifier(), rules, selected.args(), selected, ApplyMode.RELAXED).stream()
            .map(t -> searchState.withApplyResult(ctx, selected, t._2())
                .withMeta(searchState.getMeta().withExpandedQueries(searchState.getMeta().getExpandedRules() + 1))
            ).collect(Collectors.toList());

//        if (DebugStrategy.debug) {
//            if(focus != null) {
//                for(SolverState s : output) {
//                    System.out.println("- " + s.project(focus));
//                }
//            }
//        }

        return Seq.from(output);//.collect(Collectors.toList()));
    }

    /**
     * Apply the given rules to the given arguments. Returns the results of application.
     */
    public static Stream<Tuple2<Rule, ApplyResult>> applyAllLazy(
        IUniDisunifier.Immutable state,
        Collection<Rule> rules,
        List<? extends ITerm> args,
        @Nullable IConstraint cause,
        ApplyMode<VoidException> mode
    ) throws VoidException {
        return rules.stream()
            .map(rule -> {
                ApplyResult applyResult = RuleUtil.apply(state, rule, args, cause, mode).orElse(null);

                if (applyResult == null) return null;
                return Tuple2.of(rule, applyResult);
            })
            .filter(t -> t != null);
    }


}
