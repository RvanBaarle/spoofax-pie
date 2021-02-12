package mb.strategies;

import mb.sequences.Seq;

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
    public Seq<T> apply(
        CTX ctx,
        T input
    ) {
        return Seq.of(input);
    }

}


//class Strats {
//
//    @SuppressWarnings("rawtypes") private static final Strategy id = IdStrategy.getInstance();
//    @SuppressWarnings("unchecked") public static <CTX, T> Strategy<CTX, T, T> id() { return (Strategy<CTX, T, T>)id; }
//
//}
