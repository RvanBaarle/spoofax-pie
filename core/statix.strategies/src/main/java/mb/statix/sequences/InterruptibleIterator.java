package mb.statix.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * An interruptible iterator.
 *
 * @param <T> the type of values in the iterator
 */
public interface InterruptibleIterator<T> {

    /**
     * Returns whether the iteration has more elements.
     *
     * @return {@code true} if the iterator has more elements;
     * otherwise, {@code false}
     * @throws InterruptedException if the operation was interrupted
     */
    boolean hasNext() throws InterruptedException;

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iterator has no more elements
     * @throws InterruptedException if the operation was interrupted
     */
    T next() throws InterruptedException;

    /**
     * Applies the given action to each remaining value in the iterator.
     *
     * @param action the action to apply to each value
     */
    default void forEachRemaining(InterruptibleConsumer<? super T> action) throws InterruptedException {
        Objects.requireNonNull(action);
        while (hasNext()) {
            action.accept(next());
        }
    }

    /**
     * Returns an empty iterator.
     *
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    static <T> InterruptibleIterator<T> of() {
        return Constants.emptyIterator();
    }

    /**
     * Returns an iterator with the specified element.
     *
     * @param element the element in the iterator
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    static <T> InterruptibleIterator<T> of(T element) {
        return new InterruptibleIterator<T>() {
            private int index = 0;
            @Override
            public boolean hasNext() throws InterruptedException {
                return index == 0;
            }

            @Override
            public T next() throws InterruptedException {
                if (!hasNext()) throw new NoSuchElementException();
                index += 1;
                return element;
            }
        };
    }

    /**
     * Returns an iterator with the specified elements.
     *
     * @param element1 the first element in the iterator
     * @param element2 the second element in the iterator
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    static <T> InterruptibleIterator<T> of(T element1, T element2) {
        return new InterruptibleIterator<T>() {
            private int index = 0;
            @Override
            public boolean hasNext() throws InterruptedException {
                return index >= 0 && index < 2;
            }

            @Override
            public T next() throws InterruptedException {
                switch (index) {
                    case 0: index += 1; return element1;
                    case 1: index += 1; return element2;
                    default: throw new NoSuchElementException();
                }
            }
        };
    }

    /**
     * Returns an iterator with the specified elements.
     *
     * @param element1 the first element in the iterator
     * @param element2 the second element in the iterator
     * @param element3 the third element in the iterator
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    static <T> InterruptibleIterator<T> of(T element1, T element2, T element3) {
        return new InterruptibleIterator<T>() {
            private int index = 0;
            @Override
            public boolean hasNext() throws InterruptedException {
                return index >= 0 && index < 3;
            }

            @Override
            public T next() throws InterruptedException {
                switch (index) {
                    case 0: index += 1; return element1;
                    case 1: index += 1; return element2;
                    case 2: index += 1; return element3;
                    default: throw new NoSuchElementException();
                }
            }
        };
    }

    /**
     * Returns an iterator with the specified elements.
     *
     * @param elements the elements in the iterator
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    @SafeVarargs static <T> InterruptibleIterator<T> of(T... elements) {
        switch (elements.length) {
            case 0: return of();
            case 1: return of(elements[0]);
            case 2: return of(elements[0], elements[1]);
            case 3: return of(elements[0], elements[1], elements[2]);
            default: return new InterruptibleIterator<T>() {
                private int index = 0;
                @Override
                public boolean hasNext() throws InterruptedException {
                    return index >= 0 && index < elements.length;
                }

                @Override
                public T next() throws InterruptedException {
                    if (index < 0 || index >= elements.length) throw new NoSuchElementException();
                    index += 1;
                    return elements[index];
                }
            };
        }
    }

    /**
     * Turns a normal iterator into an interruptible interator.
     *
     * @param iterator the iterator to wrap
     * @param <T> the type of values in the iterator
     * @return the interruptible iterator
     */
    static <T> InterruptibleIterator<T> wrap(Iterator<T> iterator) {
        if (iterator instanceof InterruptibleIteratorUnwrapper) {
            // Return originally unwrapped interruptible iterator
            return ((InterruptibleIteratorUnwrapper<T>)iterator).iterator;
        } else {
            // Wrap normal iterator
            return new InterruptibleIteratorWrapper<>(iterator);
        }

    }

    /**
     * Turns an interruptible iterator into a normal interator.
     *
     * Note that any {@link InterruptedException} thrown in the unwrapped iterator
     * cause the thread's {@link Thread#isInterrupted()} to be set,
     * and throw a {@link RuntimeException}.
     *
     * @param iterator the iterator to unwrap
     * @param <T> the type of values in the iterator
     * @return the iterator
     */
    static <T> Iterator<T> unwrap(InterruptibleIterator<T> iterator) {
        if (iterator instanceof InterruptibleIteratorWrapper) {
            // Return originally wrapped normal iterator
            return ((InterruptibleIteratorWrapper<T>)iterator).iterator;
        } else {
            // Unwrap interruptible iterator
            return new InterruptibleIteratorUnwrapper<>(iterator);
        }
    }

}

/**
 * Wraps an {@link Iterator}.
 *
 * @param <T> the type of values in the iterator
 */
class InterruptibleIteratorWrapper<T> implements InterruptibleIterator<T> {
    final Iterator<T> iterator;

    public InterruptibleIteratorWrapper(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() throws InterruptedException {
        return iterator.hasNext();
    }

    @Override
    public T next() throws InterruptedException {
        return iterator.next();
    }
}

/**
 * Unwraps an {@link InterruptibleIterator}.
 *
 * Note that any {@link InterruptedException} thrown in the unwrapped iterator
 * cause the thread's {@link Thread#isInterrupted()} to be set,
 * and throw a {@link RuntimeException}.
 *
 * @param <T> the type of values in the iterator
 */
class InterruptibleIteratorUnwrapper<T> implements Iterator<T> {
    final InterruptibleIterator<T> iterator;

    public InterruptibleIteratorUnwrapper(InterruptibleIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        try {
            return iterator.hasNext();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted.", ex);
        }
    }

    @Override
    public T next() {
        try {
            return iterator.next();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted.", ex);
        }
    }
}
