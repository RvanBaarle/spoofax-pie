package mb.statix.lazy;

import mb.statix.sequences.InterruptibleSupplier;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A lazy sequence is a lazy computation of multiple values.
 *
 * The values in a sequence can be {@code null}.
 * The sequence can only be iterated <i>once</i>.
 *
 * The iterator is initially positioned <i>before</i> the first element,
 * and will be positioned <i>after</i> the last element once all elements
 * have been iterated.
 *
 * The iterator should be closed when done, to release any resources it holds.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
public interface LazySeq<T> extends AutoCloseable {

    /**
     * Gets the current element in the iterator.
     *
     * Note that the behavior is undefined when the iterator is not positioned
     * on a valid element. In this case, this method may return {@code null},
     * may return another value, may return an old value, or throw an exception.
     *
     * Initially the iterator is positioned <i>before</i> the first element.
     *
     * @return the current element
     */
    T getCurrent();

    /**
     * Moves to the next element in the iterator, if any.
     *
     * @return {@code true} when the iterator is now on a valid element;
     * otherwise, {@code false} when the end of the iterator has been reached
     * @throws InterruptedException if the operation was interrupted
     */
    boolean next() throws InterruptedException;

    /**
     * Returns an empty lazy sequence.
     *
     * @param <T> the type of values in the sequence (covariant)
     * @return the empty sequence
     */
    @SuppressWarnings("unchecked")
    static <T> LazySeq<T> of() {
        return EmptySeq.instance;
    }

    /**
     * Returns a lazy sequence with the specified elements.
     *
     * @param elements the elements in the sequence
     * @param <T> the type of values in the sequence (covariant)
     * @return the sequence
     */
    @SafeVarargs static <T> LazySeq<T> of(T... elements) {
        if (elements.length == 0) return of();
        return new ArraySeq<>(elements);
    }

    /**
     * Returns a lazy sequence that uses the given supplier.
     *
     * The sequence returns elements until the supplier throws an exception.
     * The thrown exception is propagated to the caller.
     * If the supplier throws {@link NoSuchElementException},
     * then the sequence is finished but no exception is thrown.
     *
     * @param supplier the supplier
     * @param <T> the type of values being supplied (covariant)
     * @return the sequence
     */
    static <T> LazySeq<T> from(InterruptibleSupplier<T> supplier) {
        return new SupplierSeq<>(supplier);
    }

    /**
     * Turns an iterator into a lazy sequence.
     *
     * @param iterator the iterator to wrap
     * @param <T> the type of values in the iterator
     * @return the lazy sequence
     */
    static <T> LazySeq<T> asSeq(Iterator<T> iterator) {
        if (iterator instanceof LazySeqIterator) {
            // Return originally wrapped lazy sequence
            return ((LazySeqIterator<T>)iterator).seq;
        } else {
            // Wrap normal iterator
            return new IteratorSeq<>(iterator);
        }
    }

    /**
     * Turns a lazy sequence into an iterator.
     *
     * Note that any {@link InterruptedException} thrown in the unwrapped iterator
     * cause the thread's {@link Thread#isInterrupted()} to be set,
     * and throw a {@link RuntimeException}.
     *
     * @param seq the sequence to unwrap
     * @param <T> the type of values in the sequence
     * @return the iterator
     */
    static <T> Iterator<T> asIterator(LazySeq<T> seq) {
        if (seq instanceof IteratorSeq) {
            // Return originally wrapped iterator
            return ((IteratorSeq<T>)seq).iterator;
        } else {
            // Unwrap interruptible iterator
            return new LazySeqIterator<>(seq);
        }
    }

}

/**
 * An empty sequence.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
final class EmptySeq<T> implements LazySeq<T> {
    @SuppressWarnings("rawtypes")
    public static EmptySeq instance = new EmptySeq();

    private EmptySeq() { }

    @Override
    public T getCurrent() {
        throw new NoSuchElementException("Positioned before or after the sequence.");
    }

    @Override
    public boolean next() throws InterruptedException {
        return false;
    }

    @Override
    public void close() {
        // Nothing to do.
    }
}

/**
 * Wraps an array.
 *
 * @param <T> the type of elements in the array (covariant)
 */
final class ArraySeq<T> implements LazySeq<T> {
    private int index = -1;
    private final T[] elements;

    public ArraySeq(T[] elements) {
        this.elements = elements;
    }

    @Override
    public T getCurrent() {
        if (index < 0 || index >= elements.length)
            throw new NoSuchElementException("Positioned before or after the sequence.");
        return elements[index];
    }

    @Override
    public boolean next() throws InterruptedException {
        index += 1;
        return index >= 0 && index < elements.length;
    }

    @Override
    public void close() {
        // Nothing to do.
    }
}

/**
 * Wraps a supplier.
 *
 * @param <T> the type of elements in the array (covariant)
 */
final class SupplierSeq<T> extends LazySeqBase<T> {
    private final InterruptibleSupplier<T> supplier;

    public SupplierSeq(InterruptibleSupplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected void computeNext() throws InterruptedException {
        try {
            final T value = this.supplier.get();
            this.yield(value);
        } catch (NoSuchElementException ex) {
            yieldBreak();
        } catch (Throwable ex) {
            yieldBreak();
            throw ex;
        }
    }

    @Override
    public void close() throws Exception {
        if (this.supplier instanceof AutoCloseable) {
            ((AutoCloseable)this.supplier).close();
        }
    }
}

/**
 * Sequence wrapping an {@link Iterator}.
 *
 * @param <T> the type of values in the iterator (covariant)
 */
final class IteratorSeq<T> extends LazySeqBase<T> {
    final Iterator<T> iterator;

    public IteratorSeq(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    protected void computeNext() throws InterruptedException {
        if (iterator.hasNext()) {
            this.yield(iterator.next());
        } else {
            yieldBreak();
        }
    }
}

/**
 * Iterator wrapping a {@link LazySeq}.
 *
 * Note that any {@link InterruptedException} thrown in the unwrapped sequence
 * cause the thread's {@link Thread#isInterrupted()} to be set,
 * and throw a {@link RuntimeException}.
 *
 * The iterator should be closed when done, to release any resources it holds.
 *
 * @param <T> the type of values in the sequence (covariant)
 */
class LazySeqIterator<T> implements Iterator<T>, AutoCloseable {
    final LazySeq<T> seq;
    private boolean consumed = false;

    public LazySeqIterator(LazySeq<T> seq) {
        this.seq = seq;
    }

    @Override
    public boolean hasNext() {
        try {
            this.consumed = false;
            return seq.next();
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted.", ex);
        }
    }

    @Override
    public T next() {
        if (this.consumed) {
            // THROWS: RuntimeException
            final boolean produced = hasNext();
            if (!produced) throw new NoSuchElementException("Sequence is finished.");
        }
        this.consumed = true;
        return seq.getCurrent();
    }

    @Override
    public void close() throws Exception {
        this.seq.close();
    }
}
