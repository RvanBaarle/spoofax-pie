package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Computation#buffer}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_BufferTests {

    @Test
    public void buffersResult_whenEvaluatedMultipleTimes() throws InterruptedException {
        // Arrange
        final AtomicInteger called = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> called.getAndIncrement()).buffer();

        // Act
        @Nullable Integer result1 = sut.tryEval();
        @Nullable Integer result2 = sut.tryEval();
        @Nullable Integer result3 = sut.tryEval();

        assertEquals(1, called.get());
        assertEquals(0, result1);
        assertEquals(0, result2);
        assertEquals(0, result3);
    }

    @Test
    public void shouldReturnSameObject_whenCallingBufferOnABufferedComputation() throws InterruptedException {
        // Arrange
        final AtomicInteger called = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> called.getAndIncrement()).buffer();

        // Act
        final Computation<Integer> doubleBuffered = sut.buffer();

        assertSame(sut, doubleBuffered);
    }

}
