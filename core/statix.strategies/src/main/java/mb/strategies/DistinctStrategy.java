package mb.strategies;

import mb.sequences.Seq;

/**
 * Ensures the results from the given strategy are distinct.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class DistinctStrategy<CTX, I, O> implements Strategy1<CTX, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final DistinctStrategy instance = new DistinctStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> DistinctStrategy<CTX, I, O> getInstance() { return (DistinctStrategy<CTX, I, O>)instance; }

    private DistinctStrategy() {}

    @Override
    public String getName() { return "distinct"; }

    @Override
    public Seq<O> apply(
        CTX ctx,
        Strategy<CTX, I, O> s,
        I input
    ) {
        return s.apply(ctx, input).distinct();
    }

}
