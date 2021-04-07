package mb.strategies;

import mb.sequences.Seq;

/**
 * Limits the number of results from the given strategy.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class LimitStrategy<CTX, I, O> extends AbstractStrategy2<CTX, Integer, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final LimitStrategy instance = new LimitStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> LimitStrategy<CTX, I, O> getInstance() { return (LimitStrategy<CTX, I, O>)instance; }

    private LimitStrategy() {}

    @Override
    public String getName() { return "limit"; }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "limit";
            case 1: return "s";
            default: return super.getParamName(index);
        }
    }

    @Override
    protected Seq<O> innerEval(
        CTX ctx,
        Integer limit,
        Strategy<CTX, I, O> s,
        I input
    ) {
        final Seq<O> values = s.eval(ctx, input);
        return values.take(limit);
    }

}
