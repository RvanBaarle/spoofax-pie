package mb.strategies;

import mb.sequences.Seq;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@FunctionalInterface
public interface Strategy<CTX, I, O> extends StrategyDecl {

    @Override
    default int getArity() { return 0; }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> apply(CTX ctx, I input);
}
