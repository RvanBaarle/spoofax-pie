package mb.statix.common.strategies;

import mb.statix.common.sequences.InterruptibleConsumer;
import mb.statix.common.sequences.Sequence;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

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
        final Sequence<O> source = s.apply(ctx, input);
        return new SequenceImpl(source);
    }

    @Override
    public <A extends Appendable> A write(A buffer) throws IOException {
        buffer.append("single(");
        s.write(buffer);
        buffer.append(")");
        return buffer;
    }

    private final class SequenceImpl implements Sequence<O> {

        /** The source sequence. */
        private final Sequence<O> source;

        /**
         * Initializes a new instance of the {@link SequenceImpl} class.
         *
         * @param source the source sequence
         */
        public SequenceImpl(Sequence<O> source) {
            this.source = source;
        }

        @Override
        public boolean tryAdvance(InterruptibleConsumer<? super O> action) throws InterruptedException {
            AtomicReference<O> value = new AtomicReference<>();
            if (!this.source.tryAdvance(value::set)) return false;  // No values
            if (this.source.tryAdvance(it -> {})) return false;     // More than one value
            action.accept(value.get());                             // Exactly one value
            return true;
        }

    }
}
