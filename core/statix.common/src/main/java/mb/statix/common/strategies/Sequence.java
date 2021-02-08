package mb.statix.common.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A sequence that returns values through its iterator.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
public interface Sequence<T> {

    /**
     * Gets the iterator that returns values from the sequence.
     *
     * Getting the iterator multiple times will compute the values multiple times.
     *
     * @return the iterator
     */
    Iterator<T> iterator();

    /**
     * Creates a sequence from an iterable.
     *
     * @param iterable the iterable
     * @param <T> the type of values
     * @return the sequence
     */
    static <T> Sequence<T> from(Iterable<T> iterable) {
        return iterable::iterator;
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
        final List<T> buffer = new ArrayList<T>();
        final Iterator<T> iterator = this.iterator();
        while (iterator.hasNext()) {
            buffer.add(iterator.next());
        }
        return buffer;
    }
}
