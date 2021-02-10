package mb.strategies;

import mb.sequences.Sequence;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Core strategies.
 */
@SuppressWarnings("unused")
public final class Strategies {
    private Strategies() {}

    /**
     * Applies a strategy to the results of the condition strategy, if any;
     * otherwise applies an alternative strategy to the original inputs.
     *
     * @param condition the condition strategy
     * @param onSuccess the strategy to apply when the condition strategy doesn't fail
     * @param onFailure the strategy to apply when the condition strategy fails
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <M> the type of intermediates
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, M, O> Strategy<CTX, I, O> glc(
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure
    ) {
        return GlcStrategy.<CTX, I, M, O>getInstance().apply(condition, onSuccess, onFailure);
    }

    /**
     * Asserts that the given strategy returns only one result.
     *
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> single(
        Strategy<CTX, I, O> s
    ) {
        return SingleStrategy.<CTX, I, O>getInstance().apply(s);
    }

    /**
     * Always succeeds and creates a sequence from the input.
     *
     * @param <CTX> the type of context
     * @param <T> the type of value
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> id() {
        return IdStrategy.getInstance();
    }

    /**
     * Always fails.
     *
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> fail() {
        return FailStrategy.getInstance();
    }

    /**
     * Composes two strategies.
     *
     * @param s the first strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> SeqStrategy.Builder<CTX, I, O> seq(
        Strategy<CTX, I, O> s
    ) {
        return new SeqStrategy.Builder<>(s);
    }

    /**
     * Limits the number of results from the given strategy.
     *
     * @param limit the limit
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> limit(
        int limit,
        Strategy<CTX, I, O> s
    ) {
        return LimitStrategy.<CTX, I, O>getInstance().apply(limit, s);
    }

    /**
     * Ensures the sequence resulting from the given strategy is distinct.
     *
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> distinct(
        Strategy<CTX, I, O> s
    ) {
        return DistinctStrategy.<CTX, I, O>getInstance().apply(s);
    }

    /**
     * Repeatedly evaluates the strategy until the set of values no longer changes.
     *
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <T> the type of values
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> fixSet(
        Strategy<CTX, T, T> s
    ) {
        return FixSetStrategy.<CTX, T>getInstance().apply(s);
    }

    /**
     * Attempts to apply the specified strategy.
     *
     * @param <CTX> the type of context
     * @param <T> the type of values
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> try_(
        Strategy<CTX, T, T> s
    ) {
        // s < id + id
        return glc(s, id(), id());
    }

    /**
     * Applies the specified strategy until it fails.
     *
     * @param <CTX> the type of context
     * @param <T> the type of values
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> repeat(
        Strategy<CTX, T, T> s
    ) {
        // try(s ; repeat(s))
        return rec(x -> try_(seq(s).$(x).$()));
    }

    /**
     * Builds a recursive strategy.
     *
     * @param f the strategy builder function, which takes a reference to the built strategy itself
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> rec(Function<Strategy<CTX, I, O>, Strategy<CTX, I, O>> f) {
        return new Strategy<CTX, I, O>() {
            @Override
            public Sequence<O> eval(CTX ctx, I input) throws InterruptedException {
                return f.apply(this).eval(ctx, input);
            }
        };
    }

    /**
     * Asserts that the input matches the given predicate.
     *
     * @param <CTX> the type of context
     * @param <T> the type of values
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> assertThat(
        Predicate<T> predicate
    ) {
        return AssertStrategy.<CTX, T>getInstance().apply(predicate);
    }
}
