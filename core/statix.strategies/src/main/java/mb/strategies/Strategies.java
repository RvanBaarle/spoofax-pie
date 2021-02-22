package mb.strategies;

import mb.sequences.Computation;
import mb.sequences.Seq;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Core strategies.
 */
@SuppressWarnings("unused")
public final class Strategies {
    private Strategies() {}

    /**
     * Asserts that the input matches the given predicate.
     *
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> all(
        Predicate<O> predicate,
        Strategy<CTX, I, O> strategy
    ) {
        return AllStrategy.<CTX, I, O>getInstance().apply(predicate, strategy);
    }

    /**
     * Accepts a consumer of the value.
     *
     * @param c the consumer
     * @param <CTX> the type of context
     * @param <T> the type of value
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> accept(
        Consumer<T> c
    ) {
        return (ctx, input) -> (Computation<T>)() -> {
            c.accept(input);
            return input;
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

    /**
     * Provides initial values.
     *
     * @param iterable the iterable
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> build(
        Iterable<O> iterable
    ) {
        return BuildStrategy.<CTX, I, O>getInstance().apply(iterable);
    }

    /**
     * Asserts that the input matches the given predicate.
     *
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> debug(
        Consumer<? super O> action,
        Strategy<CTX, I, O> strategy
    ) {
        return DebugStrategy.<CTX, I, O>getInstance().apply(action, strategy);
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
    public static <CTX, I, M, O> Strategy<CTX, I, O> if_(
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure
    ) {
        return GlcStrategy.<CTX, I, M, O>getInstance().apply(condition, onSuccess, onFailure);
    }


    public static <CTX, I, M, O> Strategy<CTX, I, O> fold(
        Strategy<CTX, I, M> s1,
        M initial,
        Strategy<CTX, M, O> operation
    ) {
        return null;
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
     * Applies two strategies to the input and concatenates the results.
     *
     * @param s1 the first strategy
     * @param s2 the second strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> or(
        Strategy<CTX, I, O> s1,
        Strategy<CTX, I, O> s2
    ) {
        return OrStrategy.<CTX, I, O>getInstance().apply(s1, s2);
    }

    /**
     * Prints all values resulting from the given strategy.
     *
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> print(String prefix, Strategy<CTX, I, O> s) {
        return debug(v -> System.out.println(prefix + v.toString()), s);
    }

    /**
     * Prints all values resulting from the given strategy.
     *
     * @param s the strategy
     * @param <CTX> the type of context
     * @param <I> the type of input
     * @param <O> the type of outputs
     * @return the resulting strategy
     */
    public static <CTX, I, O> Strategy<CTX, I, O> print(Strategy<CTX, I, O> s) {
        return print("", s);
    }

    /**
     * Prints the value.
     *
     * @param <CTX> the type of context
     * @param <T> the type of values
     * @return the resulting strategy
     */
    public static <CTX, T> Strategy<CTX, T, T> print() {
        return print(id());
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
            public Seq<O> eval(CTX ctx, I input) {
                return f.apply(this).eval(ctx, input);
            }
        };
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
        return if_(s, id(), id());
    }
}
