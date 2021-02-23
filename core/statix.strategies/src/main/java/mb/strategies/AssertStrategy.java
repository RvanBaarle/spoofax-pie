package mb.strategies;

import mb.sequences.Computation;
import mb.sequences.Seq;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Predicate;

/**
 * Asserts that the value conforms to the given predicate.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class AssertStrategy<CTX, T> extends AbstractStrategy1<CTX, Predicate<T>, T, T>{

    @SuppressWarnings("rawtypes")
    private static final AssertStrategy instance = new AssertStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> AssertStrategy<CTX, T> getInstance() { return (AssertStrategy<CTX, T>)instance; }

    private AssertStrategy() {}

    @Override
    public String getName() { return "assert"; }

    @Override
    public Seq<T> eval(
        CTX ctx,
        Predicate<T> predicate,
        T input
    ) {
        return (Computation<T>)() -> {
            if (!predicate.test(input)) return null;
            return input;
        };
    }

}
