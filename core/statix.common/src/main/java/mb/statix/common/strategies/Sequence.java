package mb.statix.common.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A sequence that returns values through its iterator.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
public interface Sequence<T> {

//    /**
//     * Gets the iterator that returns values from the sequence.
//     *
//     * Getting the iterator multiple times will compute the values multiple times.
//     *
//     * @return the iterator
//     */
//    Iterator<T> iterator();

    /**
     * Applies the specified action to values in this sequence, until the sequence is empty.
     *
     * @param action the action to apply
     * @return {@code true} when a value was present (and the action was applied); otherwise, {@code false}
     */
    boolean tryAdvance(Consumer<? super T> action);

    /**
     * Applies the specified action to the remaining values in this sequence, until the sequence is empty.
     *
     * @param action the action to apply
     */
    @SuppressWarnings("StatementWithEmptyBody")
    default void forEachRemaining(Consumer<? super T> action) {
        while (tryAdvance(action));
    }

    /**
     * Creates a sequence from an iterable.
     *
     * @param iterable the iterable
     * @param <T> the type of values
     * @return the sequence
     */
    static <T> Sequence<T> from(Iterable<T> iterable) {
        return new IteratorSequence<T>(iterable.iterator());
    }

    /**
     * Creates a sequence from the specified values.
     *
     * @param values the values
     * @param <T> the type of values
     * @return the sequence
     */
    @SafeVarargs static <T> Sequence<T> of(T... values) {
        return Sequence.from(Arrays.asList(values.clone()));
    }

    /**
     * Coerces the sequence to a list.
     *
     * @return the list
     */
    default List<T> toList() {
        final List<T> buffer = new ArrayList<>();
        this.forEachRemaining(buffer::add);
        return buffer;
    }

    static class IteratorSequence<T> implements Sequence<T> {

        private final Iterator<T> iterator;

        /**
         * Initializes a new instance of the {@link IteratorSequence} class.
         *
         * @param iterator the iterator
         */
        public IteratorSequence(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (!this.iterator.hasNext()) return false;
            final T value = this.iterator.next();
            action.accept(value);
            return true;
        }
    }
}
