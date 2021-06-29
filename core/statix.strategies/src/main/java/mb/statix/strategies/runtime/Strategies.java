package mb.statix.strategies.runtime;

import mb.statix.sequences.Seq;
import mb.statix.strategies.Strategy;
import mb.statix.strategies.Strategy1;
import mb.statix.strategies.Strategy2;
import mb.statix.strategies.Strategy3;

import java.util.function.Function;

/**
 * Strategy convenience functions.
 */
public final class Strategies {
    private Strategies() { /* Prevent instantiation. */ }

    public static <CTX, T, R> Strategy<CTX, T, R> and(
        Strategy<CTX, T, R> s1,
        Strategy<CTX, T, R> s2
    ) {
        return AndStrategy.<CTX, T, R>getInstance().apply(s1, s2);
    }

    public static <CTX, T, R> Strategy<CTX, T, R> fail() {
        return FailStrategy.<CTX, T, R>getInstance();
    }

    public static <CTX, T, U, R> Strategy<CTX,  T, R> glc(
        Strategy<CTX, T, U> condition,
        Strategy<CTX, U, R> onSuccess,
        Strategy<CTX, T, R> onFailure
    ) {
        return GlcStrategy.<CTX, T, U, R>getInstance().apply(condition, onSuccess, onFailure);
    }

    public static <CTX, T> Strategy<CTX, T, T> id() {
        return IdStrategy.<CTX, T>getInstance();
    }

    public static <CTX, T, R> Strategy<CTX, T, R> limit(
        Strategy<CTX, T, R> s,
        int n
    ) {
        return LimitStrategy.<CTX, T, R>getInstance().apply(s, n);
    }

    public static <CTX, T, R> Strategy<CTX, T, R> or(
        Strategy<CTX, T, R> s1,
        Strategy<CTX, T, R> s2
    ) {
        return OrStrategy.<CTX, T, R>getInstance().apply(s1, s2);
    }

    public static <CTX, T> Strategy<CTX, T, T> repeat(
        Strategy<CTX, T, T> s
    ) {
        return RepeatStrategy.<CTX, T>getInstance().apply(s);
    }

    public static <CTX, T> Strategy<CTX, T, T> try_(
        Strategy<CTX, T, T> s
    ) {
        return TryStrategy.<CTX, T>getInstance().apply(s);
    }

    /**
     * Builds a recursive strategy.
     *
     * @param f the strategy builder function, which takes a reference to the built strategy itself
     * @param <CTX> the type of context (invariant)
     * @param <T> the type of input (contravariant)
     * @param <R> the type of output (covariant)
     * @return the resulting strategy
     */
    public static <CTX, T, R> Strategy<CTX, T, R> rec(Function<Strategy<CTX, T, R>, Strategy<CTX, T, R>> f) {
        return new Strategy<CTX, T, R>() {
            @Override
            public Seq<R> eval(CTX ctx, T input) {
                return f.apply(this).eval(ctx, input);
            }
        };
    }
}
