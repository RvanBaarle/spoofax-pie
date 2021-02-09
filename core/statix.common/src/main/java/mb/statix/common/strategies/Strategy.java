package mb.statix.common.strategies;
import mb.statix.common.sequences.Sequence;

import java.io.IOException;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of outputs (covariant)
 */
@FunctionalInterface
public interface Strategy<CTX, I, O> {

    /**
     * Applies the strategy to the specified input.
     *
     * @param ctx the strategy context
     * @param input the strategy input
     * @return a lazy sequence of values; which may be empty when the strategy failed
     */
    Sequence<O> apply(CTX ctx, I input);

    /**
     * Writes the strategy to the specified appendable.
     *
     * @param buffer the appendable buffer
     * @param <A> the type of appendable
     * @return the buffer
     * @throws IOException if an I/O exception occurred
     */
    default <A extends Appendable> A write(A buffer) throws IOException {
        buffer.append(toString());
        return buffer;
    }

}
