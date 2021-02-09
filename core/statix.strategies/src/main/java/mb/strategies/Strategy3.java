package mb.strategies;

import mb.sequences.Sequence;

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

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param input the input value
     * @return the resulting (possibly lazy) sequence of values
     * @throws InterruptedException if the operation was interrupted
     */
    Sequence<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) throws InterruptedException;

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy2<CTX, A2, A3, I, O> apply(A1 arg1) {
        return new AppliedStrategy2<>(this, arg1);
    }

    /**
     * Partially applies the strategy, providing the first and second arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
        return new AppliedStrategy1<>(apply(arg1), arg2);
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
        return new AppliedStrategy<>(apply(arg1).apply(arg2), arg3);
    }

    /**
     * Fully applies the strategy, providing the first, second, third, and input arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param input the input
     * @return the resulting fully applied strategy
     */
    default Computation<CTX, O> apply(A1 arg1, A2 arg2, A3 arg3, I input) {
        return new AppliedComputation<>(apply(arg1).apply(arg2).apply(arg3), input);
    }
}
