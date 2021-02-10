package mb.strategies;

import mb.sequences.Sequence;

import java.util.Iterator;

/**
 * Returns the input as a sequence.
 *
 * @param <CTX> the type of context
 * @param <T> the type of value
 */
public final class IdStrategy<CTX, T> implements Strategy<CTX, T, T>{

    @SuppressWarnings("rawtypes")
    private static final IdStrategy instance = new IdStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> IdStrategy<CTX, T> getInstance() { return (IdStrategy<CTX, T>)instance; }

    private IdStrategy() {}

    @Override
    public String getName() { return "id"; }

    @Override
    public Sequence<T> eval(
        CTX ctx,
        T input
    ) throws InterruptedException {
        return Sequence.of(input);
    }

}
