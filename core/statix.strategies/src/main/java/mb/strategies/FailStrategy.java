package mb.strategies;

import mb.sequences.Seq;

/**
 * Returns the input as a sequence.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class FailStrategy<CTX, I, O> implements Strategy<CTX, I, O>{

    @SuppressWarnings("rawtypes")
    private static final FailStrategy instance = new FailStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> FailStrategy<CTX, I, O> getInstance() { return (FailStrategy<CTX, I, O>)instance; }

    private FailStrategy() {}

    @Override
    public String getName() { return "fail"; }

    @Override
    public Seq<O> eval(
        CTX ctx,
        I input
    ) {
        return Seq.empty();
    }

}
