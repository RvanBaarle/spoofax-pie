package mb.strategies;

import mb.sequences.Seq;

/**
 * Returns the results of the given strategy only when there is only one result.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class SingleStrategy<CTX, I, O> implements Strategy1<CTX, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final SingleStrategy instance = new SingleStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> SingleStrategy<CTX, I, O> getInstance() { return (SingleStrategy<CTX, I, O>)instance; }

    private SingleStrategy() {}

    @Override
    public String getName() { return "single"; }

    @Override
    public boolean isAnonymous() { return false; }

    @Override
    public Seq<O> eval(
        CTX ctx,
        Strategy<CTX, I, O> s,
        I input
    ) {
        return s.eval(ctx, input).single();
    }

    @Override
    public String toString() { return getName(); }
}
