package mb.strategies;

import mb.sequences.Sequence;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@FunctionalInterface
public interface Strategy<CTX, I, O> extends StrategyDecl {

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param input the input value
     * @return the resulting (possibly lazy) sequence of values
     * @throws InterruptedException if the operation was interrupted
     */
    Sequence<O> eval(CTX ctx, I input) throws InterruptedException;

    /**
     * Fully applies the strategy, providing the input arguments.
     *
     * @param input the input
     * @return the resulting fully applied strategy
     */
    default Computation<CTX, O> apply(I input) {
        return new AppliedComputation<>(this, input);
    }

}
