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

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param input the input value
     * @return the resulting (possibly lazy) sequence of values
     * @throws InterruptedException if the operation was interrupted
     */
    Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, I input);

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A2, I, O> apply(A1 arg1) {
        return new AppliedStrategy1<>(this, arg1);
    }

    /**
     * Partially applies the strategy, providing the first and second arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2) {
        return new AppliedStrategy<>(apply(arg1), arg2);
    }

    /**
     * Fully applies the strategy, providing the first, second, and input arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param input the input
     * @return the resulting fully applied strategy
     */
    default Computation<CTX, O> apply(A1 arg1, A2 arg2, I input) {
        return new AppliedComputation<>(apply(arg1).apply(arg2), input);
    }

}
