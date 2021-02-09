package mb.strategies;

import mb.sequences.Sequence;

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

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param input the input value
     * @return the resulting (possibly lazy) sequence of values
     * @throws InterruptedException if the operation was interrupted
     */
    Sequence<O> eval(CTX ctx, A1 arg1, I input) throws InterruptedException;

    /**
     * Partially applies the strategy, providing the first arguments.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1) {
        return new AppliedStrategy<>(this, arg1);
    }

    /**
     * Fully applies the strategy, providing the first and input arguments.
     *
     * @param arg1 the first argument
     * @param input the input
     * @return the resulting fully applied strategy
     */
    default Computation<CTX, O> apply(A1 arg1, I input) {
        return new AppliedComputation<>(apply(arg1), input);
    }

}
