package mb.statix.common.strategies;

import java.io.IOException;
import java.util.Iterator;

/**
 * Strategy that returns the specified values.
 *
 * @param <CTX> the type of context (invariant)
 * @param <O> the type of outputs (covariant)
 */
public final class BuildStrategy<CTX, O> implements Strategy<CTX, Object, O> {

    private final Iterable<O> values;

    public BuildStrategy(Iterable<O> values) {
        this.values = values;
    }

    @Override
    public Sequence<O> apply(CTX ctx, Object input) throws InterruptedException {
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
