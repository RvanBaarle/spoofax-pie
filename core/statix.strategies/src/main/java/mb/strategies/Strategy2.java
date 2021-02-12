package mb.strategies;

import mb.sequences.Seq;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <A2> the type of the second argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@FunctionalInterface
public interface Strategy2<CTX, A1, A2, I, O> extends StrategyDecl {

    @Override
    default int getArity() { return 2; }

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A2, I, O> apply(A1 arg1) {
        return new Strategy1<CTX, A2, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, A2 arg2, I input) {
                return Strategy2.this.apply(ctx, arg1, arg2, input);
            }

            @Override
            public String getName() {
                return Strategy2.this.getName();
            }

            @Override
            public Strategy<CTX, I, O> apply(A2 arg2) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return Strategy2.this.apply(arg1, arg2);
            }

            @Override
            public String toString() {
                return getName() + "(" + arg1 + ")";
            }
        };
    }

    /**
     * Partially applies the strategy, providing the first and second arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2) {
        return new Strategy<CTX, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, I input) {
                return Strategy2.this.apply(ctx, arg1, arg2, input);
            }

            @Override
            public String getName() {
                return Strategy2.this.getName();
            }

            @Override
            public String toString() {
                return getName() + "(" + arg1 + ", " + arg2 + ")";
            }
        };
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> apply(CTX ctx, A1 arg1, A2 arg2, I input);

}
