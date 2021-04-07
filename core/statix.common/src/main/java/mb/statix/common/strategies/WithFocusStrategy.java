package mb.statix.common.strategies;

import mb.nabl2.terms.ITermVar;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.strategies.AbstractStrategy2;
import mb.strategies.Strategy;

/**
 * Sets the variable focussed on in the search state.
 *
 * Verify that the resulting solver state has no errors before continueing.
 */
public final class WithFocusStrategy<I, O> extends AbstractStrategy2<SolverContext, ITermVar, Strategy<SolverContext, I, O>, I, O> {

    @SuppressWarnings("rawtypes")
    private static final WithFocusStrategy instance = new WithFocusStrategy();
    @SuppressWarnings("unchecked")
    public static <I, O> WithFocusStrategy<I, O> getInstance() { return instance; }

    private WithFocusStrategy() {}

    @Override
    protected Seq<O> innerEval(SolverContext ctx, ITermVar v, Strategy<SolverContext, I, O> s, I state) {
        return s.eval(ctx.withFocusVar(v), state);
    }

    @Override
    public String getName() {
        return "withFocus";
    }

}
