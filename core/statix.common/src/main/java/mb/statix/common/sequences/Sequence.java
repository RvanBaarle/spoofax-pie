package mb.statix.common.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A sequence that produces values.
 *
 * A sequence is an operation that can be lazily evaluated once.
 * It is similar to an {@link java.util.Iterator} (but doesn't have separate {@link Iterator#hasNext()}
 * and {@link Iterator#next()} methods, to prevent race conditions), and similar to an {@link java.util.Spliterator}
 * (but doesn't have some of the more advanced features of the spliterator, such as the ability to do parallel computation).
 *
 * This class provides many convenience methods and static function.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
@FunctionalInterface
public interface Sequence<T> {

    /**
     * Applies the specified action to values in this sequence, until the sequence is empty.
     *
     * @param action the action to apply
     * @return {@code true} when a value was present (and the action was applied); otherwise, {@code false}
     * @throws InterruptedException if the operation was interrupted
     */
    boolean tryAdvance(InterruptibleConsumer<? super T> action) throws InterruptedException;

    /**
     * Applies the specified action to the remaining values in this sequence, until the sequence is empty.
     *
     * @param action the action to apply
     * @return {@code true} when at least one value was present (and the action was applied); otherwise, {@code false}
     * @throws InterruptedException if the operation was interrupted
     */
    @SuppressWarnings("StatementWithEmptyBody")
    default boolean forEachRemaining(InterruptibleConsumer<? super T> action) throws InterruptedException {
        if (!tryAdvance(action)) return false;
        while (tryAdvance(action));
        return true;
    }

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of values
     * @return an empty sequence
     */
    // TODO: Optimize empty sequence
    static <T> Sequence<T> empty() { return of(); }

    /**
     * Creates a sequence from the specified values.
     *
     * @param values the values
     * @param <T> the type of values
     * @return a sequence
     */
    // TODO: Optimize the empty and singleton cases (separate overloads)
    @SafeVarargs static <T> Sequence<T> of(T... values) {
        return Sequence.from(Arrays.asList(values.clone()));
    }

    /**
     * Creates a sequence from an iterable.
     *
     * @param iterable the iterable
     * @param <T> the type of values
     * @return a sequence
     */
    static <T> Sequence<T> from(Iterable<T> iterable) {
        final Iterator<T> iterator = iterable.iterator();
        return action -> {
            if(!iterator.hasNext()) return false;
            final T value = iterator.next();
            action.accept(value);
            return true;
        };
    }

    /**
     * Concatenates the specified sequences in the given order.
     *
     * @param sequences the sequences to concatenate
     * @param <T> the type of values
     * @return a sequence of the concatenated sequences
     */
    @SafeVarargs static <T> Sequence<T> concat(Sequence<T>... sequences) {
        //noinspection rawtypes
        return new Sequence<T>() {
            private final Sequence[] seqs = sequences.clone();
            private int index = 0;
            @Override
            @SuppressWarnings("unchecked")
            public boolean tryAdvance(InterruptibleConsumer<? super T> action) throws InterruptedException {
                while(index < seqs.length && !seqs[index].tryAdvance(action)) {
                    index += 1;
                }
                return index < seqs.length;
            }
        };
    }

    /**
     * Maps a function over the values in the sequence.
     *
     * @param function the mapping function
     * @param <R> the type of return value
     * @return a sequence of mapped values
     */
    default <R> Sequence<R> map(InterruptibleFunction<T, R> function) {
        return action -> this.tryAdvance(it -> action.accept(function.apply(it)));
    }

    /**
     * Flat-maps a function over the values in the sequence.
     *
     * @param function the mapping function to sequences
     * @param <R> the type of return value
     * @return a sequence of mapped values
     */
    default <R> Sequence<R> flatMap(InterruptibleFunction<T, Sequence<R>> function) {
        return new Sequence<R>() {
            @Nullable private Sequence<R> curSeq = null;
            @Override
            public boolean tryAdvance(InterruptibleConsumer<? super R> action) throws InterruptedException {
                if (this.curSeq == null) {
                    // First time, get the first sequence
                    Sequence.this.tryAdvance(it -> this.curSeq = function.apply(it));
                }
                // If the current sequence is not empty, apply the action and advance
                while (this.curSeq != null && !this.curSeq.tryAdvance(action)) {
                    // The current sequence was empty
                    this.curSeq = null;     // For safety, if tryAdvance misbehaves we will not end up in an infinite loop
                    // Get the next sequence
                    if (!Sequence.this.tryAdvance(it -> this.curSeq = function.apply(it))) {
                        // There is no next sequence, we're done
                        return false;
                    }
                }
                // The current sequence was not empty. (Or it was null.)
                return this.curSeq != null;
            }
        };
    }

    /**
     * Concatenates this sequence with the specified sequence.
     *
     * @param other the other sequence
     * @return the concatenated sequence
     */
    default Sequence<T> concatWith(Sequence<T> other) {
        return concat(Sequence.this, other);
    }

    /**
     * Coerces the sequence to a list.
     *
     * @return the list
     * @throws InterruptedException if the operation was interrupted
     */
    default List<T> toList() throws InterruptedException {
        final List<T> buffer = new ArrayList<>();
        this.forEachRemaining(buffer::add);
        return buffer;
    }

}
