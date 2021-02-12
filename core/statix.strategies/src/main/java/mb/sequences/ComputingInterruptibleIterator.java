package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;

/**
 * An iterator that computes its results in advance.
 */
public abstract class ComputingInterruptibleIterator<T> extends InterruptibleIteratorBase<T>{
    @Nullable private Iterator<T> iterator = null;

    protected abstract Iterable<T> computeAll() throws InterruptedException;

    @Override
    protected void computeNext() throws InterruptedException {
        if (this.iterator == null) {
            // Compute the elements
            this.iterator = computeAll().iterator();
        }
        // Yield the elements
        if (this.iterator.hasNext()) {
            setNext(this.iterator.next());
        } else {
            finished();
        }
    }
}
