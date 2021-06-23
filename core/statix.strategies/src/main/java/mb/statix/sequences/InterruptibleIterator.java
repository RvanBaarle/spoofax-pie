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

    boolean hasNext() throws InterruptedException;

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
