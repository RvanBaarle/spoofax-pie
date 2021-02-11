package mb.strategies;

import mb.sequences.Sequence;

/**
 * A computation.
 *
 * @param <CTX> the type of context (invariant)
 * @param <O> the type of output (covariant)
 */
public interface Computation<CTX, O> extends StrategyDecl {

    /**
     * Evaluates the computation.
     *
     * @param ctx the context
     * @return the resulting (possibly lazy) sequence of values
     * @throws InterruptedException if the operation was interrupted
     */
    Sequence<O> eval(CTX ctx) throws InterruptedException;

}