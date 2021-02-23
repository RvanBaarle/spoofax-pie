package mb.statix.strategies;

import com.google.common.collect.ImmutableSet;
import mb.nabl2.terms.ITermVar;
import mb.sequences.Seq;
import mb.statix.common.FocusedSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.CUser;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;
import mb.strategies.AbstractStrategy;
import mb.strategies.Strategy;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy extends AbstractStrategy<SolverContext, FocusedSolverState<CUser>, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final ExpandRuleStrategy instance = new ExpandRuleStrategy();
    @SuppressWarnings("unchecked")
    public static ExpandRuleStrategy getInstance() { return (ExpandRuleStrategy)instance; }

    private ExpandRuleStrategy() {}

    @Override
    public String getName() {
        return "expandRule";
    }

    @Override
    public Seq<SolverState> eval(SolverContext ctx, FocusedSolverState<CUser> state) {
        CUser focus = state.getFocus();
        System.out.println("Expand rule: " + focus);

        final ImmutableSet<Rule> rules = ctx.getSpec().rules().getOrderIndependentRules(focus.name());
        SolverState searchState = state.getInnerState();
        List<SolverState> output = RuleUtil.applyAll(searchState.getState(), rules, focus.args(), focus).stream()
            .map(t -> searchState.withApplyResult(t._2(), focus)).collect(Collectors.toList());

        @Nullable final ITermVar focusVar = ctx.getFocusVar();
        if (focusVar != null) {
            for(SolverState s : output) {
                System.out.println("- " + s.project(focusVar));
            }
        }

        return Seq.from(output);
    }


}
