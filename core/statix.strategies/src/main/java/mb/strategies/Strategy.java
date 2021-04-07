package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Supplier;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy<CTX, I, O> extends StrategyDecl {

    /**
     * Defines a named strategy with no arguments.
     *
     * @param name the name of the strategy
     * @param builder the strategy builder, which takes no arguments
     * @param <CTX> the type of context (invariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the built strategy
     */
    static <CTX, I, O> Strategy<CTX, I, O> define(String name, Supplier<Strategy<CTX, I, O>> builder) {
        return builder.get().withName(name);
    }

    /**
     * Names the strategy.
     *
     * @param name the strategy name
     * @return the named strategy
     */
    default Strategy<CTX, I, O> withName(String name) {
        // Wraps a strategy and gives it a name.
        return new AbstractStrategy<CTX, I, O>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            protected Seq<O> innerEval(CTX ctx, I input) {
                return Strategy.this.eval(ctx, input);
            }

            @Override
            public Strategy<CTX, I, O> withName(String name) {
                // Delegate to the inner strategy, to avoid wrapping twice
                return Strategy.this.withName(name);
            }
        };
    }

    @Override
    default int getArity() { return 0; }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, I input);
}
