package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Function;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy1<CTX, A1, I, O> extends StrategyDecl {

    /**
     * Defines a named strategy with one argument.
     *
     * @param name the name of the strategy
     * @param builder the strategy builder, which takes one argument
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the built strategy
     */
    static <CTX, A1, I, O> Strategy1<CTX, A1, I, O> define(String name, Function<A1, Strategy<CTX, I, O>> builder) {
        // Wraps a strategy builder, and gives it a name.
        return new AbstractStrategy1<CTX, A1, I, O>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Strategy<CTX, I, O> apply(A1 arg1) {
                return builder.apply(arg1).withName(name);
            }

            @Override
            public Seq<O> eval(CTX ctx, A1 arg1, I input) {
                return apply(arg1).eval(ctx, input);
            }

            @Override
            public Strategy1<CTX, A1, I, O> withName(String name) {
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
    default Strategy1<CTX, A1, I, O> withName(String name) {
        // Wraps a strategy and gives it a name.
        return new AbstractStrategy1<CTX, A1, I, O>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Seq<O> eval(CTX ctx, A1 arg1, I input) {
                return Strategy1.this.eval(ctx, arg1, input);
            }

            @Override
            public Strategy1<CTX, A1, I, O> withName(String name) {
                // Delegate to the inner strategy, to avoid wrapping twice
                return Strategy1.this.withName(name);
            }
        };
    }

    @Override
    default int getArity() { return 1; }

    /**
     * Partially applies the strategy, providing the first arguments.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1) {
        return new AppliedStrategies.ApplStrategy1To0<CTX, A1, I, O>(Strategy1.this, arg1);
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, A1 arg1, I input);

}
