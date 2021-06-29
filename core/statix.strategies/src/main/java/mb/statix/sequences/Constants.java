package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.NoSuchElementException;

/**
 * Constants for sequences and iterators.
 */
/* package private */ class Constants {
    private Constants() { /* Cannot be instantiated. */ }

    /**
     * Gets an always empty computation.
     *
     * @param <T> the type of elements in the computation
     * @return the empty computation
     */
    @SuppressWarnings("unchecked")
    public static <T> Computation<T> emptyComputation() {
        return (Computation<T>)EmptyComputation.instance;
    }

    /**
     * Gets an always empty iterator.
     *
     * @param <T> the type of elements in the iterator
     * @return the empty iterator
     */
    @SuppressWarnings("unchecked")
    public static <T> InterruptibleIterator<T> emptyIterator() {
        return (InterruptibleIterator<T>)EmptyIterator.instance;
    }

    /**
     * An always empty sequence.
     *
     * @param <T> the type of elements in the sequence
     */
    private static class EmptyComputation<T> implements Computation<T> {
        @SuppressWarnings("rawtypes")
        public static EmptyComputation instance = new EmptyComputation();

        @Override
        public @Nullable T tryEval() throws InterruptedException {
            return null;
        }

        @Override
        public T eval() throws InterruptedException {
            throw new IllegalStateException("The computation always fails.");
        }

        @Override
        public InterruptibleIterator<T> iterator() {
            return emptyIterator();
        }
    }

    /**
     * An always empty iterator.
     * @param <T> the type of elements in the iterator
     */
    private static class EmptyIterator<T> implements InterruptibleIterator<T> {
        @SuppressWarnings("rawtypes")
        public static EmptyIterator instance = new EmptyIterator();

        @Override
        public boolean hasNext() throws InterruptedException {
            return false;
        }

        @Override
        public T next() throws InterruptedException {
            throw new NoSuchElementException("This iterator is always empty.");
        }
    }
}
