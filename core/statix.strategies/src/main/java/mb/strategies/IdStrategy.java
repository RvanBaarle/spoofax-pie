package mb.strategies;

import mb.sequences.Seq;

/**
 * Returns the input as a sequence.
 *
 * @param <CTX> the type of context
 * @param <T> the type of value
 */
public final class IdStrategy<CTX, T> extends AbstractStrategy<CTX, T, T>{

    @SuppressWarnings("rawtypes")
    private static final IdStrategy instance = new IdStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> IdStrategy<CTX, T> getInstance() { return (IdStrategy<CTX, T>)instance; }

    private IdStrategy() {}

    @Override
    public String getName() { return "id"; }

    @Override
    protected Seq<T> innerEval(
        CTX ctx,
        T input
    ) {
        return Seq.of(input);
    }

}