package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Predicate;

/**
 * Asserts that the value conforms to the given predicate.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class AssertStrategy<CTX, T> implements Strategy1<CTX, Predicate<T>, T, T>{

    @SuppressWarnings("rawtypes")
    private static final AssertStrategy instance = new AssertStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> AssertStrategy<CTX, T> getInstance() { return (AssertStrategy<CTX, T>)instance; }

    private AssertStrategy() {}

    @Override
    public String getName() { return "assert"; }

    @Override
    public Seq<T> apply(
        CTX ctx,
        Predicate<T> predicate,
        T input
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
