package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Computation#constrainOnce}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_ConstrainOnceTests {

    @Test
    public void throws_whenEvaluatedMultipleTimes() throws InterruptedException {
        // Arrange
        final AtomicInteger called = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> called.getAndIncrement()).constrainOnce();

        // Act
        @Nullable Integer result1 = sut.tryEval();
        assertThrows(IllegalStateException.class, () -> {
            sut.tryEval();
        });

        assertEquals(1, called.get());
        assertEquals(0, result1);
    }

    @Test
    public void shouldReturnSameObject_whenCallingConstrainOnceOnAConstrainedComputation() throws InterruptedException {
        // Arrange
        final AtomicInteger called = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> called.getAndIncrement()).constrainOnce();

        // Act
        final Computation<Integer> doubleConstrained = sut.constrainOnce();

        assertSame(sut, doubleConstrained);
    }

}
