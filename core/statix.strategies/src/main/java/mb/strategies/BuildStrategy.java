package mb.strategies;

import mb.sequences.Seq;

/**
 * Provides initial values, ignoring the input.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class BuildStrategy<CTX, I, O> implements Strategy1<CTX, Iterable<O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final BuildStrategy instance = new BuildStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> BuildStrategy<CTX, I, O> getInstance() { return (BuildStrategy<CTX, I, O>)instance; }

    private BuildStrategy() {}

    @Override
    public String getName() { return "build"; }

    @Override
    public Seq<O> apply(
        CTX ctx,
        Iterable<O> iterable,
        I input
    ) {
        return Seq.from(iterable);
    }

}
