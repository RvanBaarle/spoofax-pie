package mb.sequences;

/**
 * A continuation.
 *
 * @param <T> the type of result (contravariant)
 */
@FunctionalInterface
public interface Continuation<T> {

    /**
     * Resumes the continuation with the given value.
     *
     * @param value the value
     */
    void resume(T value);

//    /**
//     * Fails the continuation with the given exception.
//     *
//     * @param exception the exception
//     */
//    void fail(Throwable exception);

}
