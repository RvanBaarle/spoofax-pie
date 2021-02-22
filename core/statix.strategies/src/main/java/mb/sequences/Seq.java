package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A sequence represents a lazy computation of multiple values.
 *
 * The values in a sequence are recomputed on every iteration.
 * To prevent this, insert a call to {@link #buffer}.
 *
 * The values in the sequence could technically be {@code null},
 * but this is not supported.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
@SuppressWarnings({"Convert2Diamond"}) @FunctionalInterface
public interface Seq<T> {

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of results
     * @return a sequence
     */
    static <T> Seq<T> fail() {
        return () -> InterruptibleIterator.empty();
    }

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of results
     * @return an empty sequence
     */
    static <T> Seq<T> empty() {
        return fail();
    }

    /**
     * Returns an empty sequence.
     *
     * @param <T> the type of results
     * @return an empty sequence
     */
    static <T> Seq<T> of() {
        return fail();
    }

    /**
     * Returns a sequence with the specified result.
     *
     * @param result the result
     * @param <T>    the type of results
     * @return a sequence
     */
    static <T> Seq<T> of(T result) {
        Objects.requireNonNull(result);

        return Seq.from(Collections.singletonList(result));
    }

    /**
     * Returns a sequence with the specified results.
     *
     * @param results the results
     * @param <T>     the type of results
     * @return a sequence
     */
    @SafeVarargs static <T> Seq<T> of(T... results) {
        Objects.requireNonNull(results);

        if(results.length == 0) return fail();

        List<T> list = new ArrayList<>(results.length);
        for(final T result : results) {
            Objects.requireNonNull(result);
            list.add(result);
        }

        return Seq.from(list);
    }

    /**
     * Returns a sequence that sources from the specified iterable.
     *
     * @param iterable the iterable
     * @param <T>      the type of results
     * @return a sequence
     */
    static <T> Seq<T> from(Iterable<T> iterable) {
        Objects.requireNonNull(iterable);

        return () -> InterruptibleIterator.wrap(iterable.iterator());
    }

    /**
     * Returns a sequence that sources from the specified supplier.
     *
     * @param supplier the supplier of results
     * @param <T> the type of results
     * @return a sequence
     */
    static <T> Seq<T> from(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);

        return () -> new InterruptibleIteratorBase<T>() {
            @Override
            protected void computeNext() throws InterruptedException {
                setNext(supplier.get());
            }
        };
    }

//    /**
//     * Returns a sequence that sources from the specified supplier.
//     *
//     * @param supplier the supplier of results
//     * @param <T> the type of results
//     * @return a sequence
//     */
//    static <T> Seq<T> compute(Supplier<InterruptibleIterator<T>> supplier) {
//        Objects.requireNonNull(supplier);
//
//        return () -> new InterruptibleIteratorBase<T>() {
//            @Nullable private InterruptibleIterator<T> iterator = null;
//            @Override
//            protected void computeNext() throws InterruptedException {
//                if (this.iterator == null) {
//                    this.iterator = supplier.get();
//                }
//                if (this.iterator.hasNext()) {
//                    setNext(this.iterator.next());
//                } else {
//                    finished();
//                }
//            }
//        };
//    }

    /**
     * Returns a sequence that concatenates the sequences in the order they are specified.
     *
     * @param sequences the sequences to concatenate
     * @return the new sequence
     */
    @SafeVarargs static <T> Seq<T> concat(Seq<T>... sequences) {
        Objects.requireNonNull(sequences);

        return () -> new InterruptibleIteratorBase<T>() {
            @Nullable private InterruptibleIterator<T> iterator = null;
            private int index = 0;

            @Override
            protected void computeNext() throws InterruptedException {
                // Return the next element
                if(iterator != null && iterator.hasNext()) {
                    setNext(iterator.next());
                    return;
                }
                while(index < sequences.length) {
                    // Get the next iterator
                    iterator = sequences[index].iterator();
                    index += 1;
                    // Return its first element, if any
                    if(iterator.hasNext()) {
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
     * An iterator for this sequence.
     *
     * The returned iterator is not thread-safe.
     *
     * @return the iterator
     */
    InterruptibleIterator<T> iterator();

    /**
     * Returns a sequence that buffers all its elements.
     *
     * This ensures the sequence is not recomputed.
     *
     * Note that this sequence is not thread-safe!
     *
     * @return the new sequence
     */
    default Seq<T> buffer() {
        return new Seq<T>() {
            private final List<T> buffer = new ArrayList<>();
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();

            @Override
            public InterruptibleIterator<T> iterator() {
                return new InterruptibleIteratorBase<T>() {
                    int index = 0;

                    @Override
                    protected void computeNext() throws InterruptedException {
                        // FIXME: This computation is not thread-safe.
                        // It is possible for two threads to use the same sequence
                        // and cause a race condition in the getting and storing the next element.
                        final T value;
                        if(index >= 0 && index < buffer.size()) {
                            // Return element from the buffer
                            value = buffer.get(index);
                        } else {
                            if(!iterator.hasNext()) {
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
    default Seq<T> constrainOnce() {
        return new Seq<T>() {
            private InterruptibleIterator<T> iterator = Seq.this.iterator();

            @Override
            public InterruptibleIterator<T> iterator() {
                if(this.iterator == null) return new InterruptibleIteratorBase<T>() {
                    @Override
                    protected void computeNext() {
                        throw new IllegalStateException("Detected multiple iteration of this sequence.");
                    }
                };
                final InterruptibleIterator<T> currentIterator = this.iterator;
                this.iterator = null;
                return currentIterator;
            }
        };
    }

    /**
     * Returns an iterable wrapping the sequence.
     *
     * Note that any {@link InterruptedException} thrown in the sequence computation
     * cause the thread's {@link Thread#isInterrupted()} to be set,
     * and throw a {@link RuntimeException}.
     *
     * @return the iterable
     */
    default Iterable<T> asIterable() {
        return () -> InterruptibleIterator.unwrap(Seq.this.iterator());
    }

    /**
     * Returns a computation that determines whether all the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return a computation that returns {@code true} when all elements match the predicate;
     * otherwise, {@code false}
     */
    default Computation<Boolean> all(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> {
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                if(!predicate.test(value)) return false;
            }
            return true;
        };
    }

    /**
     * Returns a computation that determines whether any the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return a computation that returns {@code true} when any elements match the predicate;
     * otherwise, {@code false}
     */
    default Computation<Boolean> any(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> {
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                if(predicate.test(value)) return true;
            }
            return false;
        };
    }

    /**
     * Returns a computation that determines whether the sequence has any elements.
     *
     * @return a computation that returns {@code true} when the sequence is not empty;
     * otherwise, {@code false}
     */
    default Computation<Boolean> any() {
        return () -> this.iterator().hasNext();
    }

    /**
     * Returns a computation that determines whether none the elements in the sequence match the given predicate.
     *
     * @param predicate the predicate to test
     * @return a computation that returns {@code true} when none elements match the predicate;
     * otherwise, {@code false}
     */
    default Computation<Boolean> none(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> {
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                if(predicate.test(value)) return false;
            }
            return true;
        };
    }

    /**
     * Returns a computation that determines whether the sequence has no elements.
     *
     * @return a computation that returns {@code true} when the sequence is empty;
     * otherwise, {@code false}
     */
    default Computation<Boolean> none() {
        return () -> !this.iterator().hasNext();
    }

    /**
     * Returns a computation that determines whether the sequence has no elements.
     *
     * @return a computation that returns {@code true} when the sequence is empty;
     * otherwise, {@code false}
     */
    default Computation<Boolean> isEmpty() {
        return none();
    }

    /**
     * Returns a computation that determines whether the sequence has any elements.
     *
     * @return a computation that returns {@code true} when the sequence is not empty;
     * otherwise, {@code false}
     */
    default Computation<Boolean> isNotEmpty() {
        return any();
    }

    /**
     * Returns a computation that creates a list with all the elements from the sequence.
     *
     * @return a computation that returns the list of elements
     */
    default Computation<List<T>> toList() {
        return () -> {
            final List<T> list = new ArrayList<T>();
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                list.add(value);
            }
            return list;
        };
    }

    /**
     * Returns a computation that creates a set with all the elements from the sequence.
     *
     * @return a computation that returns the set of elements
     */
    default Computation<Set<T>> toSet() {
        return () -> {
            // We use a LinkedHashSet to preserve insertion order
            final Set<T> set = new LinkedHashSet<T>();
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                set.add(value);
            }
            return set;
        };
    }

    /**
     * Returns a sequence that concatenates this sequence with the specified sequence.
     *
     * @param other the other sequence
     * @return the concatenated sequence
     */
    default Seq<T> concatWith(Seq<T> other) {
        Objects.requireNonNull(other);

        return concat(Seq.this, other);
    }

    /**
     * Returns a computation that determines whether the sequence contains any element equal to the given element.
     *
     * @param element the element to find, which may be {@code null}
     * @return a computation that returns {@code true} if an equal element was found; otherwise, {@code false}
     */
    default Computation<Boolean> contains(@Nullable T element) {
        return any(it -> (element == null && it == null) || (element != null && element.equals(it)));
    }

    /**
     * Returns a computation of the number of elements in the sequence.
     *
     * @return a computation that returns the number of elements in the sequence
     */
    default Computation<Integer> count() {
        return () -> {
            int count = 0;
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                iterator.next();
                count += 1;
            }
            return count;
        };
    }

    /**
     * Returns a computation of the number of elements in the sequence that match the given predicate
     *
     * @param predicate the predicate to test
     * @return a computation that returns the number of elements in the sequence that match the given predicate
     */
    default Computation<Integer> count(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> {
            int count = 0;
            final InterruptibleIterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                final T value = iterator.next();
                if(predicate.test(value)) count += 1;
            }
            return count;
        };
    }

    /**
     * Prints the sequence to the console.
     *
     * @param transform the function that transforms the sequence elements to a string
     * @return the printed (and buffered) sequence
     */
    default Seq<T> debug(Function<T, String> transform) {
        Seq<T> results = Seq.this.buffer();
        try {
            List<T> resultsList = results.toList().eval();
            if(resultsList.isEmpty()) {
                System.out.println("failed");
            } else {
                System.out.println(resultsList.size() + " results");
                for(T result : resultsList) {
                    System.out.println("- " + transform.apply(result));
                }
            }
        } catch (InterruptedException ex) {
            System.out.println("INTERRUPTED");
        }
        return results;
    }

    /**
     * Returns a sequence with only distinct elements.
     *
     * @return the distinct sequence
     */
    default Seq<T> distinct() {
        return distinctBy(Function.identity());
    }

    /**
     * Returns a sequence with only elements distinct by the given key.
     *
     * @param selector the key selector
     * @param <K>      the type of key
     * @return the distinct sequence
     */
    default <K> Seq<T> distinctBy(Function<T, K> selector) {
        Objects.requireNonNull(selector);

        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();
            private final HashSet<K> observed = new HashSet<>();

            @Override
            protected void computeNext() throws InterruptedException {
                while(iterator.hasNext()) {
                    final T value = iterator.next();
                    final K key = selector.apply(value);

                    if(observed.add(key)) {
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
    default Seq<T> drop(int n) {
        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();
            private int skipped = 0;

            @Override
            protected void computeNext() throws InterruptedException {
                // Skip the first n elements
                while(skipped < n && iterator.hasNext()) {
                    iterator.next();
                    skipped += 1;
                }

                // Return the rest
                if(iterator.hasNext()) {
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
    default Seq<T> dropWhile(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();
            private boolean done = false;

            @Override
            protected void computeNext() throws InterruptedException {
                // Skip the first elements
                while(!done && iterator.hasNext()) {
                    T value = iterator.next();
                    if(!predicate.test(value)) {
                        done = true;
                        break;
                    }
                }

                // Return the rest
                if(iterator.hasNext()) {
                    setNext(iterator.next());
                    return;
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
    default Seq<T> take(int n) {
        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();
            private int taken = 0;

            @Override
            protected void computeNext() throws InterruptedException {
                // Return the first n elements
                if(taken < n && iterator.hasNext()) {
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
    default Seq<T> takeWhile(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();

            @Override
            protected void computeNext() throws InterruptedException {
                // Return the first elements
                if(iterator.hasNext()) {
                    T value = iterator.next();
                    if(predicate.test(value)) {
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
     * Returns a sequence that contains only those elements that match the given predicate.
     *
     * @param predicate the predicate to test
     * @return the new sequence
     */
    default Seq<T> filter(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return () -> new InterruptibleIteratorBase<T>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();

            @Override
            protected void computeNext() throws InterruptedException {
                // Return elements that match the predicate
                while(iterator.hasNext()) {
                    T value = iterator.next();
                    if(predicate.test(value)) {
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
     * This operation buffers all results.
     *
     * @param initial the initial value
     * @param operation the operation function
     * @return the new sequence
     */
    default <R> Computation<R> fold(R initial, InterruptibleBiFunction<R, T, R> operation) {
        Objects.requireNonNull(operation);

        return new Computation<R>() {
            @Override
            public @Nullable R tryEval() throws InterruptedException {
                R acc = initial;
                final InterruptibleIterator<T> iterator = Seq.this.iterator();
                while(iterator.hasNext()) {
                    final T value = iterator.next();
                    acc = operation.apply(acc, value);
                }
                return acc;
            }
        };
    }

    /**
     * Returns a sequence with the results from applying a transform function to each of the original elements.
     *
     * @param transform the transform function
     * @return the new sequence
     */
    default <R> Seq<R> map(InterruptibleFunction<T, R> transform) {
        Objects.requireNonNull(transform);

        return () -> new InterruptibleIteratorBase<R>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();

            @Override
            protected void computeNext() throws InterruptedException {
                // Return elements that match the predicate
                if(iterator.hasNext()) {
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
    default <R> Seq<R> flatMap(InterruptibleFunction<T, Seq<R>> transform) {
        Objects.requireNonNull(transform);

        return () -> new InterruptibleIteratorBase<R>() {
            private final InterruptibleIterator<T> iterator = Seq.this.iterator();
            @Nullable private InterruptibleIterator<R> nestedIterator = null;

            @Override
            protected void computeNext() throws InterruptedException {
                while(true) {
                    // Return elements from the nested iterator
                    if(nestedIterator != null && nestedIterator.hasNext()) {
                        R value = nestedIterator.next();
                        setNext(value);
                        return;
                    }

                    // Find the next nested iterator
                    if(!iterator.hasNext()) break;
                    T value = iterator.next();
                    Seq<R> newValue = transform.apply(value);
                    nestedIterator = newValue.iterator();
                }

                // The iterator is empty
                finished();
            }
        };
    }

    /**
     * Returns a sequence of the only result to which the predicate applied,
     * or an empty sequence when the predicate applies to more or less than one result.
     *
     * @param predicate the predicate to apply
     * @return the new sequence
     */
    default Seq<T> single(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);

        return filter(predicate).single();
    }

    /**
     * Returns a computation of the only result,
     * or a failed computation when the sequence has more than one result.
     *
     * @return the new computation
     */
    default Computation<T> single() {
        return new Computation<T>() {
            @Override
            public @Nullable T tryEval() throws InterruptedException {
                final InterruptibleIterator<T> iterator = Seq.this.iterator();

                if (!iterator.hasNext()) {
                    // The source has no elements, we're done.
                    return null;
                }

                final T result = iterator.next();
                if (iterator.hasNext()) {
                    // The source has more than one element.
                    return null;
                }

                return result;
            };
        };
    }

}
