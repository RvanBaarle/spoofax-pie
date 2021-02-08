package mb.statix.common.strategies;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Evaluates a strategy and returns its values only if it results in exactly one value.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of outputs (covariant)
 */
public final class SingleStrategy<CTX, I, O> implements Strategy<CTX, I, O> {

    private final Strategy<CTX, I, O> s;

    /**
     * Initializes a new instance of the {@link SingleStrategy} class.
     *
     * @param s the strategy
     */
    public SingleStrategy(Strategy<CTX, I, O> s) {
        this.s = s;
    }

    @Override
    public Sequence<O> apply(CTX ctx, I input) throws InterruptedException {
        final Sequence<O> sequence = s.apply(ctx, input);
        return () -> new IteratorImpl(sequence.iterator());
    }

    @Override
    public <A extends Appendable> A write(A buffer) throws IOException {
        buffer.append("single(");
        s.write(buffer);
        buffer.append(")");
        return buffer;
    }

    /**
     * Iterator implementation that buffers the values
     * until either the specified limit has been reached
     * or the source produces no more values.
     *
     * This class is not thread-safe.
     */
    private class IteratorImpl implements Iterator<O> {

        /** The source iterator. */
        private final Iterator<O> source;
        /** A singleton list, or {@code null} when not computed. */
        @Nullable private List<O> buffer = null;

        /**
         * Initializes a new instance of the {@link IteratorImpl} class.
         *
         * @param source the source iterator
         */
        public IteratorImpl(Iterator<O> source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            return !fillAndReturnBuffer().isEmpty();
        }

        @Override
        public O next() {
            final List<O> buffer = fillAndReturnBuffer();
            if (buffer.isEmpty()) throw new NoSuchElementException();
            final O value = buffer.get(0);
            // No more values remaining
            this.buffer = Collections.emptyList();
            return value;
        }

        private List<O> fillAndReturnBuffer() {
            if (this.buffer != null) return this.buffer;    // Already done

            List<O> buffer;
            if (this.source.hasNext()) {
                // Buffer the first value
                buffer = Collections.singletonList(this.source.next());

                if(this.source.hasNext()) {
                    // The source produced more than one value
                    // so we fail.
                    buffer = Collections.emptyList();
                }
            } else {
                // The source was empty
                buffer = Collections.emptyList();
            }

            // Done
            this.buffer = buffer;
            return buffer;
        }
    }
}
