package mb.strategies;

import mb.sequences.Seq;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@FunctionalInterface
public interface Strategy1<CTX, A1, I, O> extends StrategyDecl {

    @Override
    default int getArity() { return 1; }

    /**
     * Partially applies the strategy, providing the first arguments.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1) {
        return new Strategy<CTX, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, I input) {
                return Strategy1.this.apply(ctx, arg1, input);
            }

            @Override
            public String getName() {
                return Strategy1.this.getName();
            }

            @Override
            public String toString() {
                return getName() + "(" + arg1 + ")";
            }
        };
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> apply(CTX ctx, A1 arg1, I input);

}
