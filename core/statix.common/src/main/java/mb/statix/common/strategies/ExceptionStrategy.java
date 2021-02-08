package mb.statix.common.strategies;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Strategy that always throws an exception.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 */
public final class ExceptionStrategy<CTX, I> implements Strategy<CTX, I, Void> {

    private final BiFunction<CTX, I, RuntimeException> exceptionBiFunction;

    /**
     * Initializes a new instance of the {@link ExceptionStrategy} class.
     *
     * @param exceptionSupplier supplies the runtime exception
     */
    public ExceptionStrategy(Supplier<RuntimeException> exceptionSupplier) {
        this((ctx, i) -> exceptionSupplier.get());
    }

    /**
     * Initializes a new instance of the {@link ExceptionStrategy} class.
     *
     * @param exceptionBiFunction given the context and input, supplies the runtime exception
     */
    public ExceptionStrategy(BiFunction<CTX, I, RuntimeException> exceptionBiFunction) {
        this.exceptionBiFunction = exceptionBiFunction;
    }

    @Override
    public Sequence<Void> apply(CTX ctx, I input) throws InterruptedException {
        return () -> { throw exceptionBiFunction.apply(ctx, input); };
    }

    @Override
    public <A extends Appendable> A write(A buffer) throws IOException {
        buffer.append("<error>");
        return buffer;
    }
}
