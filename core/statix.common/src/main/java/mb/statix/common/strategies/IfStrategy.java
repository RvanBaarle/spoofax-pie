package mb.statix.common.strategies;

import mb.statix.common.sequences.InterruptibleConsumer;
import mb.statix.common.sequences.Sequence;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Evaluates a strategy and returns its values only if it results in exactly one value.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <M> the type of intermediates (invariant)
 * @param <O> the type of outputs (covariant)
 */
public final class IfStrategy<CTX, I, M, O> implements Strategy<CTX, I, O> {

    private final Strategy<CTX, I, M> condition;
    private final Strategy<CTX, M, O> onSuccess;
    private final Strategy<CTX, I, O> onFailure;

    /**
     * Initializes a new instance of the {@link IfStrategy} class.
     *
     * @param condition the condition strategy
     * @param onSuccess the strategy to apply to the results of {@code condition} if it succeeds
     * @param onFailure the strategy to apply to the input if {@code condition} fails
     */
    public IfStrategy(
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure
    ) {
        this.condition = condition;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
    }

    @Override
    public Sequence<O> apply(CTX ctx, I input) {
        return new Sequence<O>() {
            @Nullable Sequence<O> seq = null;

            @Override
            public boolean tryAdvance(InterruptibleConsumer<? super O> action) throws InterruptedException {
                // We assign to this.seq the sequence that will produce the values for this strategy
                // It is either the concatenation of the sequence from `onSuccess` flat mapped to `condition`
                // or just the `onFailure` sequence.
                // In case of success, we will have removed the first value from the `condition` sequence,
                // so we need to concat that before the rest of the `conditionSeq` to get the whole sequence again.
                if (this.seq == null) {
                    // First attempt to get an element
                    final Sequence<M> conditionSeq = condition.apply(ctx, input);

                    // Save the first element
                    AtomicReference<M> firstValue = new AtomicReference<>();
                    if (conditionSeq.tryAdvance(firstValue::set)) {
                        // Condition succeeded
                        this.seq = Sequence.concat(Sequence.of(firstValue.get()), conditionSeq).flatMap(it -> onSuccess.apply(ctx, it));
                    } else {
                        // Condition failed
                        this.seq = onFailure.apply(ctx, input);
                    }
                }
                return this.seq.tryAdvance(action);
            }
        };
    }

    @Override
    public <A extends Appendable> A write(A buffer) throws IOException {
        this.condition.write(buffer);
        buffer.append(" < ");
        this.onSuccess.write(buffer);
        buffer.append(" + ");
        this.onFailure.write(buffer);
        return buffer;
    }

}
