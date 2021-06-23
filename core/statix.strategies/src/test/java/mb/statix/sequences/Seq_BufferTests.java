package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#buffer}.
 */
public final class Seq_BufferTests {

    @Test
    public void returnsBufferedElements_whenEvaluatatedMultipleTimes() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> buffered = input.buffer();

        // Assert
        assertEquals(Arrays.asList(0), buffered.take(1).toList().tryEval());
        assertEquals(Arrays.asList(0, 1), buffered.take(2).toList().tryEval());
        assertEquals(Arrays.asList(0, 1, 2), buffered.take(3).toList().tryEval());
        assertEquals(Arrays.asList(0, 1, 2, 3), buffered.take(4).toList().tryEval());
        assertEquals(4, i.get());
    }

    @Test
    public void returnsBufferedElements_whenEvaluatatedFiniteSequence() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement).take(4);

        // Act
        Seq<Integer> buffered = input.buffer();

        // Assert
        assertEquals(Arrays.asList(0, 1, 2, 3), buffered.toList().tryEval());
        assertEquals(Arrays.asList(0, 1, 2, 3), buffered.toList().tryEval());
        assertEquals(Arrays.asList(0, 1, 2, 3), buffered.toList().tryEval());
        assertEquals(4, i.get());
    }

}
