package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Computation#eval}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_EvalTests {

    @Test
    public void returnsValue_whenComputationSucceeded() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.fromOnly(() -> 42);

        // Act
        final Integer result = sut.eval();

        // Assert
        assertEquals(42, result);
    }

    @Test
    public void throws_whenComputationFailed() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.fromOnly(() -> null);

        // Act/Assert
        assertThrows(IllegalStateException.class, () -> {
            sut.eval();
        });
    }

    @Test
    public void doesNotCompute_whileNotEvaluated() throws InterruptedException {
        // Arrange
        final AtomicBoolean called = new AtomicBoolean(false);
        final Computation<Integer> sut = Computation.fromOnly(() -> { called.set(true); return 42; });

        // Act/Assert
        assertFalse(called.get());
        sut.eval();
        assertTrue(called.get());
    }

    @Test
    public void recomputes_whenEvalIsCalled() throws InterruptedException {
        // Arrange
        final AtomicInteger calls = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> { calls.incrementAndGet(); return 42; });

        // Act
        sut.eval();
        sut.eval();
        sut.eval();

        // Assert
        assertEquals(3, calls.get());
    }

}
