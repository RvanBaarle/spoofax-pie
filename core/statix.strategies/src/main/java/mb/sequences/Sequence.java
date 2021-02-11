package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A sequence.
 *
 * The sequence implements {@link Iterable} for convenience.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
@SuppressWarnings("WhileLoopReplaceableByForEach") @FunctionalInterface
public interface Sequence<T> extends Iterable<T> {

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
        Objects.requireNonNull(values);

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
        Objects.requireNonNull(iterable);

        return iterable::iterator;
    }

    /**
     * Returns whether all the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return {@code true} when all elements match the predicate; otherwise, {@code false}
     */
    default boolean all(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        final Iterator<T> iterator = this.iterator();
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
        Objects.requireNonNull(predicate);

        final Iterator<T> iterator = this.iterator();
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
        return this.iterator().hasNext();
    }

    /**
     * Returns whether none the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return {@code true} when none elements match the predicate; otherwise, {@code false}
     */
    default boolean none(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        final Iterator<T> iterator = this.iterator();
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
        return !this.iterator().hasNext();
    }

    /**
     * Returns whether the sequence has no elements.
     *
     * @return {@code true} when the sequence is empty; otherwise, {@code false}
     */
    default boolean isEmpty() { return none(); }

    /**
     * Returns whether the sequence has any elements.
     *
     * @return {@code true} when the sequence is not empty; otherwise, {@code false}
     */
    default boolean isNotEmpty() { return any(); }

    /**
     * Returns an iterable wrapping the sequence.
     *
     * @return the iterable
     */
    default Iterable<T> toIterable() {
        return this::iterator;
    }

    /**
     * Returns a list with all the elements from the sequence.
     *
     * @return the list of elements
     */
    default List<T> toList() {
        final List<T> list = new ArrayList<T>();
        final Iterator<T> iterator = this.iterator();
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
        // We use a LinkedHashSet to preverse insertion order
        final Set<T> set = new LinkedHashSet<T>();
        final Iterator<T> iterator = this.iterator();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            set.add(value);
        }
        return set;
    }

    /**
     * Concatenates this sequence with the specified sequence.
     *
     * @param other the other sequence
     * @return the concatenated sequence
     */
    default Sequence<T> concatWith(Sequence<T> other) {
        Objects.requireNonNull(other);

        return concat(Sequence.this, other);
    }

    /**
     * Returns whether the sequence contains any element equal to the given element.
     *
     * @param element the element to find, which may be {@code null}
     * @return {@code true} if an equal element was found; otherwise, {@code false}
     */
    default boolean contains(@Nullable T element) {
        return any(element::equals);
    }

    /**
     * Returns the number of elements in the sequence.
     *
     * @return the number of elements in the sequence
     */
    default int count() {
        int count = 0;
        final Iterator<T> iterator = this.iterator();
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
        Objects.requireNonNull(predicate);

        int count = 0;
        final Iterator<T> iterator = this.iterator();
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
        Objects.requireNonNull(selector);

        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.iterator();
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
     * Returns a sequence that takes the first number of elements.
     *
     * @param n the number of elements to take
     * @return the new sequence
     */
    default Sequence<T> take(int n) {
        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.iterator();
            private int taken = 0;

            @Override
            protected void computeNext() {
                // Return the first n elements
                if (taken < n && iterator.hasNext()) {
                    setNext(iterator.next());
                    taken += 1;
                    return;
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence that takes the first elements that match the given predicate.
     *
     * @param predicate the predicate to test
     * @return the new sequence
     */
    default Sequence<T> takeWhile(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.iterator();

            @Override
            protected void computeNext() {
                // Return the first elements
                if (iterator.hasNext()) {
                    T value = iterator.next();
                    if (predicate.test(value)) {
                        setNext(value);
                        return;
                    }
                }

                // We are done
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
            private final Iterator<T> iterator = Sequence.this.iterator();
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
        Objects.requireNonNull(predicate);

        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.iterator();
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
        Objects.requireNonNull(predicate);

        return () -> new IteratorBase<T>() {
            private final Iterator<T> iterator = Sequence.this.iterator();

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
    default <R> Sequence<R> map(InterruptibleFunction<T, R> transform) {
        Objects.requireNonNull(transform);

        return () -> new IteratorBase<R>() {
            private final Iterator<T> iterator = Sequence.this.iterator();

            @Override
            protected void computeNext() throws InterruptedException {
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
    default <R> Sequence<R> flatMap(InterruptibleFunction<T, Sequence<R>> transform) {
        Objects.requireNonNull(transform);

        return () -> new IteratorBase<R>() {
            private final Iterator<T> iterator = Sequence.this.iterator();
            @Nullable private Iterator<R> nestedIterator = null;

            @Override
            protected void computeNext() throws InterruptedException {
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
                    nestedIterator = newValue.iterator();
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Concatenates the sequences in the order they are specified.
     *
     * @param sequences the sequences to concatenate
     * @return the new sequence
     */
    @SafeVarargs static <T> Sequence<T> concat(Sequence<T>... sequences) {
        Objects.requireNonNull(sequences);

        return () -> new IteratorBase<T>() {
            @Nullable private Iterator<T> iterator = null;
            private int index = 0;

            @Override
            protected void computeNext() throws InterruptedException {
                // Return the next element
                if (iterator != null && iterator.hasNext()) {
                    setNext(iterator.next());
                    return;
                }
                while(index < sequences.length) {
                    // Get the next iterator
                    iterator = sequences[index].iterator();
                    index += 1;
                    // Return its first element, if any
                    if (iterator.hasNext()) {
                        setNext(iterator.next());
                        return;
                    }
                }
                // No more sequences or elements
                finished();
            }
        };
    }

    /**
     * Returns a sequence that buffers all its elements.
     *
     * This ensures the sequence is only iterated once.
     * This can be used, for example, to avoid an expensive recomputation.
     *
     * Note that this sequence is not thread-safe!
     *
     * @return the new sequence
     */
    default Sequence<T> buffer() {
        return new Sequence<T>() {
            private final List<T> buffer = new ArrayList<>();
            private final Iterator<T> iterator = iterator();

            @Override
            public Iterator<T> iterator() {
                return new IteratorBase<T>() {
                    int index = 0;
                    @Override
                    protected void computeNext() throws InterruptedException {
                        // FIXME: This computation is not thread-safe.
                        // It is possible for two threads to use the same sequence
                        // and cause a race condition in the getting and storing the next element.
                        final T value;
                        if (index >= 0 && index < buffer.size()) {
                            // Return element from the buffer
                            value = buffer.get(index);
                        } else {
                            if (!iterator.hasNext()) {
                                finished();
                                return;
                            }
                            value = iterator.next();
                            buffer.add(value);
                        }
                        index += 1;
                        setNext(value);
                    }
                };
            }
        };
    }

    /**
     * Returns a sequence that can only be iterated once.
     *
     * This can be used, for example, to avoid an expensive recomputation
     * and to find places where this happens.
     *
     * @return the new sequence
     */
    default Sequence<T> iterateOnlyOnce() {
        return new Sequence<T>() {
            private Iterator<T> iterator = iterator();

            @Override
            public Iterator<T> iterator() {
                if (this.iterator == null) return new IteratorBase<T>() {
                    @Override
                    protected void computeNext()  {
                        throw new IllegalStateException("Detected multiple iteration of this sequence.");
                    }
                };
                final Iterator<T> currentIterator = this.iterator;
                this.iterator = null;
                return currentIterator;
            }
        };
    }
}