package mb.statix.strategies.runtime;

import mb.statix.sequences.Seq;
import mb.statix.strategies.NamedStrategy2;
import mb.statix.strategies.Strategy;

/**
 * Limiting strategy.
 *
 * This evaluates returns at most the number of elements specified in the limit.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
public final class LimitStrategy<CTX, T, R> extends NamedStrategy2<CTX, Strategy<CTX, T, R>, Integer, T, R> {

    @SuppressWarnings("rawtypes")
    private static final LimitStrategy instance = new LimitStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T, R> LimitStrategy<CTX, T, R> getInstance() { return (LimitStrategy<CTX, T, R>)instance; }

    private LimitStrategy() { /* Prevent instantiation. Use getInstance(). */ }

    @Override
    public Seq<R> eval(CTX ctx, Strategy<CTX, T, R> s, Integer n, T input) {
        final Seq<R> s1Seq = s.eval(ctx, input);
        return s1Seq.take(n);
    }

    @Override
    public String getName() {
        return "limit";
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s";
            case 1: return "n";
            default: return super.getParamName(index);
        }
    }
}
