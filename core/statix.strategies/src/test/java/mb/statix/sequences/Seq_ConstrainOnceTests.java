package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Seq#constrainOnce}.
 */
public final class Seq_ConstrainOnceTests {

    @Test
    public void throws_whenEvaluatatedMultipleTimes() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> constrained = input.constrainOnce();

        // Assert
        assertEquals(Arrays.asList(0, 1, 2, 3), constrained.take(4).toList().tryEval());
        assertThrows(IllegalStateException.class, () -> {
            constrained.take(4).toList().tryEval();
        });
        assertEquals(4, i.get());
    }

    @Test
    public void shouldReturnSameObject_whenCallingConstrainOnceOnAConstrainedSequence() throws InterruptedException {
        // Arrange
        final AtomicInteger i = new AtomicInteger();
        final Seq<Integer> sut = Seq.from(i::getAndIncrement).constrainOnce();

        // Act
        final Seq<Integer> doubleConstrained = sut.constrainOnce();

        assertSame(sut, doubleConstrained);
    }

}
