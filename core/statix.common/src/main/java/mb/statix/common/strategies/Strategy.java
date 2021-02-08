package mb.statix.common.strategies;
import java.io.IOException;
import java.util.Set;

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
     * @return a sequence of values; or an empty sequence when the strategy failed
     * @throws InterruptedException if the operation was interrupted
     */
    Sequence<O> apply(CTX ctx, I input) throws InterruptedException;

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
