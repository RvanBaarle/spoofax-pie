package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.NoSuchElementException;

/**
 * Base class for iterators.
 *
 * @param <T> the type of elements in the iterator
 */
public abstract class InterruptibleIteratorBase<T> implements InterruptibleIterator<T> {

    /**
     * Specifies the state of the iterator.
     */
    private enum State {
        /** The iterator has computed the next element. */
        Ready,
        /** The iterator has not yet computed the next element. */
        Preparing,
        /** The iterator has finished. */
        Finished,
    }

    private State state = State.Preparing;

    @Nullable private T nextValue = null;

    @Override
    public final boolean hasNext() throws InterruptedException {
        switch (state) {
            case Ready: return true;
            case Finished: return false;
            default: return tryComputeNext();
        }
    }

    @Override
    public final T next() throws InterruptedException {
        if (!hasNext()) throw new NoSuchElementException();
        this.state = State.Preparing;
        return nextValue;
    }

    /**
     * Attempts to compute the next element.
     *
     * @return {@code true} when a next element was computed;
     * otherwise, {@code false}
     */
    private boolean tryComputeNext() throws InterruptedException{
        state = State.Preparing;
        computeNext();
        return state == State.Ready;
    }

    /**
     * Computes the next element for the iterator.
     *
     * This method should call either {@link #setNext} to return the next element,
     * or {@link #finished} to indicate the end of the iterator.
     */
    protected abstract void computeNext() throws InterruptedException;

    /**
     * Indicates what the next element will be.
     *
     * @param value the next element
     */
    protected void setNext(T value) {
        this.nextValue = value;
        this.state = State.Ready;
    }

    /**
     * Indicates that the iterator is done.
     */
    protected void finished() {
        this.state = State.Finished;
    }
}
