package mb.strategies;

import mb.sequences.Sequence;

import java.util.Iterator;

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
    public Sequence<O> eval(
        CTX ctx,
        Strategy<CTX, I, O> s,
        I input
    ) throws InterruptedException {
        final Sequence<O> values = s.eval(ctx, input);
        final Iterator<O> iterator = values.iterator();
        if (!iterator.hasNext()) {
            // The source has no elements, we're done.
            return Sequence.empty();
        }
        final O value = iterator.next();
        if (iterator.hasNext()) {
            // The source has more than one element.
            return Sequence.empty();
        }
        return Sequence.of(value);
    }

}
