package mb.statix.strategies.search;

import com.google.common.collect.ImmutableSet;
import mb.nabl2.terms.ITermVar;
import mb.statix.common.FocusedSolverState;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.Strategy;
import mb.statix.constraints.CUser;
import mb.statix.spec.Rule;
import mb.statix.spec.RuleUtil;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Expands the selected rule.
 */
public final class ExpandRuleStrategy implements Strategy<FocusedSolverState<CUser>, SolverState, SolverContext> {

    public ExpandRuleStrategy() {

    }

    @Override
    public List<SolverState> apply(SolverContext ctx, FocusedSolverState<CUser> state) {
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

        return output;
    }

    @Override
    public String toString() {
        return "expandRule";
    }

}
