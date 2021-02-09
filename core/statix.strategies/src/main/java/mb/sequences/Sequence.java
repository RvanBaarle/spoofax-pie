package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A sequence.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
@FunctionalInterface
public interface Sequence<T> {

    /**
     * Gets the sequence iterator.
     *
     * @return the iterator
     */
    Iterator<T> getIterator();

    /**
     * Returns whether all the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return {@code true} when all elements match the predicate; otherwise, {@code false}
     */
    default boolean all(Predicate<T> predicate) {
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (!predicate.test(value)) return false;
        }
        return true;
    }

    /**
     * Returns whether any the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return {@code true} when any elements match the predicate; otherwise, {@code false}
     */
    default boolean any(Predicate<T> predicate) {
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (predicate.test(value)) return true;
        }
        return false;
    }

    /**
     * Returns whether the sequence has any elements.
     *
     * @return {@code true} when the sequence is not empty; otherwise, {@code false}
     */
    default boolean any() {
        return this.getIterator().hasNext();
    }

    /**
     * Returns whether none the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return {@code true} when none elements match the predicate; otherwise, {@code false}
     */
    default boolean none(Predicate<T> predicate) {
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (predicate.test(value)) return false;
        }
        return true;
    }

    /**
     * Returns whether the sequence has no elements.
     *
     * @return {@code true} when the sequence is empty; otherwise, {@code false}
     */
    default boolean none() {
        return !this.getIterator().hasNext();
    }

    /**
     * Returns an iterable wrapping the sequence.
     *
     * @return the iterable
     */
    default Iterable<T> toIterable() {
        return this::getIterator;
    }

    /**
     * Returns a list with all the elements from the sequence.
     *
     * @return the list of elements
     */
    default List<T> toList() {
        final List<T> list = new ArrayList<T>();
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            list.add(value);
        }
        return list;
    }

    /**
     * Returns a set with all the elements from the sequence.
     *
     * @return the set of elements
     */
    default Set<T> toSet() {
        final Set<T> set = new HashSet<T>();
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            set.add(value);
        }
        return set;
    }

    /**
     * Returns whether the sequence contains any element equal to the given element.
     *
     * @param element the element to find
     * @return {@code true} if an equal element was found; otherwise, {@code false}
     */
    default boolean contains(T element) {
        return any(element::equals);
    }

    /**
     * Returns the number of elements in the sequence.
     *
     * @return the number of elements in the sequence
     */
    default int count() {
        int count = 0;
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            iterator.next();
            count += 1;
        }
        return count;
    }

    /**
     * Returns the number of elements in the sequence that match the given predicate
     *
     * @param predicate the predicate to test
     * @return the number of elements in the sequence that match the given predicate
     */
    default int count(Predicate<T> predicate) {
        int count = 0;
        final Iterator<T> iterator = this.getIterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (predicate.test(value)) count += 1;
        }
        return count;
    }

    /**
     * Returns a sequence with only distinct elements.
     *
     * @return the distinct sequence
     */
    default Sequence<T> distinct() {
        return distinctBy(Function.identity());
    }

    /**
     * Returns a sequence with only elements distinct by the given key.
     *
     * @param selector the key selector
     * @return the distinct sequence
     * @param <K> the type of key
     */
    default <K> Sequence<T> distinctBy(Function<T, K> selector) {
        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();
            private final HashSet<K> observed = new HashSet<>();

            @Override
            protected void computeNext() {
                while (iterator.hasNext()) {
                    final T value = iterator.next();
                    final K key = selector.apply(value);

                    if (observed.add(key)) {
                        // The key was not yet present
                        setNext(value);
                        return;
                    }
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence that skips the first number of elements.
     *
     * @param n the number of elements to skip
     * @return the new sequence
     */
    default Sequence<T> skip(int n) {
        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();
            private int skipped = 0;

            @Override
            protected void computeNext() {
                // Skip the first n elements
                while (skipped < n && iterator.hasNext()) {
                    iterator.next();
                    skipped += 1;
                }

                // Return the rest
                if (iterator.hasNext()) {
                    setNext(iterator.next());
                    return;
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence that skips the first elements that match the given predicate.
     *
     * @param predicate the predicate to test
     * @return the new sequence
     */
    default Sequence<T> skipWhile(Predicate<T> predicate) {
        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();
            private boolean done = false;

            @Override
            protected void computeNext() {
                // Skip the first elements
                while (!done && iterator.hasNext()) {
                    T value = iterator.next();
                    if (!predicate.test(value)) {
                        done = true;
                        break;
                    }
                }

                // Return the rest
                if (iterator.hasNext()) {
                    setNext(iterator.next());
                    return;
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence that contains only those elements that match the given predicate.
     *
     * @param predicate the predicate to test
     * @return the new sequence
     */
    default Sequence<T> filter(Predicate<T> predicate) {
        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();

            @Override
            protected void computeNext() {
                // Return elements that match the predicate
                while (iterator.hasNext()) {
                    T value = iterator.next();
                    if (predicate.test(value)) {
                        setNext(value);
                        return;
                    }
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence with the results from applying a transform function to each of the original elements.
     *
     * @param transform the transform function
     * @return the new sequence
     */
    default <R> Sequence<R> map(Function<T, R> transform) {
        return () -> new IteratorBase<R>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();

            @Override
            protected void computeNext() {
                // Return elements that match the predicate
                if (iterator.hasNext()) {
                    T value = iterator.next();
                    R newValue = transform.apply(value);
                    setNext(newValue);
                    return;
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence with all results from applying a transform function to each of the original elements.
     *
     * @param transform the transform function
     * @return the new sequence
     */
    default <R> Sequence<R> flatMap(Function<T, Sequence<R>> transform) {
        return () -> new IteratorBase<R>() {
            private final Iterator<T> iterator = Sequence.this.getIterator();
            @Nullable private Iterator<R> nestedIterator = null;

            @Override
            protected void computeNext() {
                while (true) {
                    // Return elements from the nested iterator
                    if (nestedIterator != null && nestedIterator.hasNext()) {
                        R value = nestedIterator.next();
                        setNext(value);
                        return;
                    }

                    // Find the next nested iterator
                    if(!iterator.hasNext()) break;
                    T value = iterator.next();
                    Sequence<R> newValue = transform.apply(value);
                    nestedIterator = newValue.getIterator();
                }

                // The iterator is empty
                finished();
            }
        };
    }
}
