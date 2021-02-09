package mb.statix.common.strategies;

import mb.statix.common.sequences.Sequence;

import java.io.IOException;

/**
 * Strategy that returns the specified values.
 *
 * @param <CTX> the type of context (invariant)
 * @param <O> the type of input (contravariant)
 * @param <O> the type of outputs (covariant)
 */
public final class BuildStrategy<CTX, I, O> implements Strategy<CTX, I, O> {

    private final Iterable<O> values;

    public BuildStrategy(Iterable<O> values) {
        this.values = values;
    }

    @Override
    public Sequence<O> apply(CTX ctx, I input) {
        return Sequence.from(values);
    }

    @Override
    public <A extends Appendable> A write(A buffer) throws IOException {
        buffer.append("!");
        buffer.append(values.toString());
        buffer.append("");
        return buffer;
    }
}
