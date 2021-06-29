package mb.statix.sequences;

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
        /** The iterator has not yet computed the next element. */
        Preparing,
        /** The iterator has computed the next element. */
        Ready,
        /** The iterator has no more elements. */
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
     * This method should call either {@link #yield} to return the next element,
     * or {@link #yieldBreak} to indicate the end of the iterator.
     */
    protected abstract void computeNext() throws InterruptedException;

    /**
     * Indicates what the next element will be.
     *
     * Only one element can be the next element.
     * The caller must return from the method.
     *
     * @param value the next element
     */
    protected void yield(T value) {
        assert state == State.Preparing : "Only one call to either yield() or yieldBreak() is allowed per iteration.";
        this.nextValue = value;
        this.state = State.Ready;
    }

    /**
     * Indicates that the iterator is done.
     *
     * The iterator can only finish once.
     * The caller must return from the method.
     */
    protected void yieldBreak() {
        assert state == State.Preparing : "Only one call to either yield() or yieldBreak() is allowed per iteration.";
        // Set to null to release any object from this iterator for garbage collection.
        this.nextValue = null;
        this.state = State.Finished;
    }
}
