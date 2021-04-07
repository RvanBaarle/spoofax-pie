package mb.strategies;

import mb.sequences.Seq;

import static mb.strategies.Strategies.id;
import static mb.strategies.Strategies.if_;

/**
 * Applies a strategy, and returns the results of this application if it succeeded;
 * otherwise, the original input.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class TryStrategy<CTX, T> extends AbstractStrategy1<CTX, Strategy<CTX, T, T>, T, T> {

    @SuppressWarnings("rawtypes")
    private static final TryStrategy instance = new TryStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> TryStrategy<CTX, T> getInstance() { return (TryStrategy<CTX, T>)instance; }

    private TryStrategy() {}

    @Override
    protected Seq<T> innerEval(CTX ctx, Strategy<CTX, T, T> s, T input) {
        return if_(s, id(), id()).eval(ctx, input);
    }

    @Override
    public String getName() {
        return "try";
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches") @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s";
            default: return super.getParamName(index);
        }
    }
}
