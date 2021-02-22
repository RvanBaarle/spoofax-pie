package mb.strategies;

import mb.sequences.Function3;
import mb.sequences.Seq;

import java.io.IOException;
import java.util.function.BiFunction;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <A2> the type of the second argument (contravariant)
 * @param <A3> the type of the third argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy3<CTX, A1, A2, A3, I, O> extends StrategyDecl {

    /**
     * Defines a named strategy with three arguments.
     *
     * @param name the name of the strategy
     * @param builder the strategy builder, which takes three arguments
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the built strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy3<CTX, A1, A2, A3, I, O> define(String name, Function3<A1, A2, A3, Strategy<CTX, I, O>> builder) {
        // Wraps a strategy builder, and gives it a name.
        return new Strategy3<CTX, A1, A2, A3, I, O>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public Strategy<CTX, I, O> apply(A1 arg1, A2 arg2, A3 arg3) {
                return builder.apply(arg1, arg2, arg3);
            }

            @Override
            public Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) {
                return apply(arg1, arg2, arg3).eval(ctx, input);
            }

            @Override
            public String toString() {
                return name;
            }

            @Override
            public Strategy3<CTX, A1, A2, A3, I, O> withName(String name) {
                // Delegate to the inner strategy, to avoid wrapping twice
                return define(name, builder);
            }
        };
    }

    /**
     * Names the strategy.
     *
     * @param name the strategy name
     * @return the named strategy
     */
    default Strategy3<CTX, A1, A2, A3, I, O> withName(String name) {
        // Wraps a strategy and gives it a name.
        return new Strategy3<CTX, A1, A2, A3, I, O>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) {
                return Strategy3.this.eval(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public String toString() {
                return name;
            }

            @Override
            public Strategy3<CTX, A1, A2, A3, I, O> withName(String name) {
                // Delegate to the inner strategy, to avoid wrapping twice
                return Strategy3.this.withName(name);
            }
        };
    }

    @Override
    default int getArity() { return 3; }

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy2<CTX, A2, A3, I, O> apply(A1 arg1) {
        return new AppliedStrategies.ApplStrategy3To2<CTX, A1, A2, A3, I, O>(Strategy3.this, arg1);
    }

    /**
     * Partially applies the strategy, providing the first and second arguments.
     *
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
        return new AppliedStrategies.ApplStrategy3To1<CTX, A1, A2, A3, I, O>(Strategy3.this, arg1, arg2);
    }

    /**
     * Partially applies the strategy, providing the first, second, and third arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2, A3 arg3) {
        return new AppliedStrategies.ApplStrategy3To0<CTX, A1, A2, A3, I, O>(Strategy3.this, arg1, arg2, arg3);
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input);
}
