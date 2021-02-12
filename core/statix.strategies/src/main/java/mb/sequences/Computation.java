package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A computation represents a lazy computation of a single value,
 * optionally throwing an {@link InterruptedException}.
 *
 * The result of the computation could technically be {@code null},
 * but distinguishing between a {@code null} result and no result
 * is not supported for performance reasons.
 *
 * @param <T> the type of value
 */
@FunctionalInterface
public interface Computation<T> extends Seq<T> {

    /**
     * Evaluates the computation
     * and returns either its result or {@code null} when it failed.
     *
     * This method is not thread-safe.
     *
     * @return the result;
     * or {@code null} when the computation failed
     * @throws InterruptedException if the computation was interrupted
     */
    @Nullable T tryEval() throws InterruptedException;

    /**
     * Evaluates the computation,
     * asserting that the computation succeeded.
     *
     * This method is not thread-safe.
     *
     * @return the result
     * @throws IllegalStateException if the computation failed
     * @throws InterruptedException if the computation was interrupted
     */
    default T eval() throws InterruptedException {
        @Nullable final T result = tryEval();
        if (result == null) throw new IllegalStateException("The computation failed.");
        return result;
    }

    /**
     * Returns a computation that failed.
     *
     * @param <T> the type of result
     * @return a failed computation
     */
    static <T> Computation<T> fail() { return () -> null; }

    /**
     * Returns a computation that failed.
     *
     * @param <T> the type of result
     * @return a failed computation
     */
    static <T> Computation<T> empty() { return fail(); }

    /**
     * Returns a computation that failed.
     *
     * @param <T> the type of result
     * @return a failed computation
     */
    static <T> Computation<T> of() { return fail(); }

    /**
     * Returns a constant computation.
     *
     * @param result the result, which may be {@code null}
     * @param <T> the type of result
     * @return a computation
     */
    static <T> Computation<T> of(T result) {
        Objects.requireNonNull(result);

        return () -> result;
    }

    /**
     * Returns a computation that sources from the specified supplier.
     *
     * @param supplier the supplier of results
     * @param <T> the type of results
     * @return a computation
     */
    static <T> Computation<T> from(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);

        return supplier::get;
    }

    @Override
    default InterruptibleIterator<T> iterator() {
        // Iterator that evaluates the computation
        // and returns a singleton iterator (if the result was not null)
        // or an empty iterator (if the result was null).
        return new InterruptibleIteratorBase<T>() {
            private boolean done = false;
            @Override
            protected void computeNext() throws InterruptedException {
                if (!this.done) {
                    this.done = true;
                    @Nullable T value = tryEval();
                    if (value != null) {
                        setNext(value);
                        return;
                    }
                }
                finished();
            }
        };
    }

    @Override
    default Computation<T> buffer() {
        return new Computation<T>() {
            private boolean done = false;
            @Nullable private T value;
            @Override
            public @Nullable T tryEval() throws InterruptedException {
                if (!this.done) {
                    this.done = true;
                    this.value = Computation.this.tryEval();
                }
                return this.value;
            }

            @Override
            public Computation<T> buffer() {
                return this;
            }
        };
    }

    @Override
    default Seq<T> constrainOnce() {
        return new Computation<T>() {
            private boolean done = false;

            @Override
            public @Nullable T tryEval() throws InterruptedException {
                if (!this.done) {
                    this.done = true;
                    return Computation.this.tryEval();
                }
                throw new IllegalStateException("Detected multiple evaluation of this computation.");
            }

            @Override
            public Seq<T> constrainOnce() {
                return this;
            }
        };
    }
}
