package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Computation#iterator}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_IteratorTests {

    @Test
    public void returnsIteratorThatLazilyEvaluates() throws InterruptedException {
        // Arrange
        final AtomicInteger called = new AtomicInteger();
        final Computation<Integer> sut = Computation.fromOnly(() -> called.getAndIncrement());

        // Act/Assert
        final InterruptibleIterator<Integer> iterator = sut.iterator();
        assertEquals(0, called.get());
        assertTrue(iterator.hasNext());
        assertEquals(1, called.get());
        assertEquals(0, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void returnsIteratorThatDealsWithNull() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.fromOnly(() -> null);

        // Act/Assert
        final InterruptibleIterator<Integer> iterator = sut.iterator();
        assertFalse(iterator.hasNext());
    }

}
