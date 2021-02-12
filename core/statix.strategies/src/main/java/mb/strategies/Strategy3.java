package mb.strategies;

import mb.sequences.Seq;

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
@FunctionalInterface
public interface Strategy3<CTX, A1, A2, A3, I, O> extends StrategyDecl {

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
        return new Strategy2<CTX, A2, A3, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, A2 arg2, A3 arg3, I input) {
                return Strategy3.this.apply(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public String getName() {
                return Strategy3.this.getName();
            }

            @Override
            public Strategy1<CTX, A3, I, O> apply(A2 arg2) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return Strategy3.this.apply(arg1, arg2);
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
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
        return new Strategy1<CTX, A3, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, A3 arg3, I input) {
                return Strategy3.this.apply(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public String getName() {
                return Strategy3.this.getName();
            }

            @Override
            public Strategy<CTX, I, O> apply(A3 arg3) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return Strategy3.this.apply(arg1, arg2, arg3);
            }

            @Override
            public String toString() {
                return getName() + "(" + arg1 + ", " + arg2 + ")";
            }
        };
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
        return new Strategy<CTX, I, O>() {
            @Override
            public Seq<O> apply(CTX ctx, I input) {
                return Strategy3.this.apply(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public String getName() {
                return Strategy3.this.getName();
            }

            @Override
            public String toString() {
                return getName() + "(" + arg1 + ", " + arg2 + ", " + arg3 + ")";
            }
        };
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
    Seq<O> apply(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input);
}
