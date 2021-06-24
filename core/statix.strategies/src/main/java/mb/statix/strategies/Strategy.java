package mb.statix.strategies;

import mb.statix.sequences.Seq;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
@FunctionalInterface
public interface Strategy<CTX, T, R> extends StrategyDecl, PrintableStrategy {

    @Override
    default int getArity() { return 0; }

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param input the input argument
     * @return the lazy sequence of results; or an empty sequence if the strategy failed
     */
    Seq<R> eval(CTX ctx, T input);

}
