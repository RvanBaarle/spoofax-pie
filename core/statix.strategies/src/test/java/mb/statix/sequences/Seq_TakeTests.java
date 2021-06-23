package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#take}.
 */
public final class Seq_TakeTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.take(4);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(0, 1, 2, 3), result.toList().eval());
        assertEquals(4, i.get());
    }

    @Test
    public void shouldTakeTheFirstNElements() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d", "a", "b",
            "a", "b", "c", "d"
        ));

        // Act
        Seq<String> result = input.take(6);

        // Assert
        assertEquals(Arrays.asList(
            "a", "b", "c", "d", "a", "b"
        ), result.toList().eval());
    }

    @Test
    public void shouldTakeNoElements_whenNIsZero() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d", "a", "b",
            "a", "b", "c", "d"
        ));

        // Act
        Seq<String> result = input.take(0);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldTakeNoElements_whenNIsNegative() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d", "a", "b",
            "a", "b", "c", "d"
        ));

        // Act
        Seq<String> result = input.take(-1337);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldTakeAllElements_whenNIsLargerThanTheSequence() throws InterruptedException {
        // Arrange
        final List<String> values = Arrays.asList(
            "a", "b", "c", "d", "a", "b",
            "a", "b", "c", "d"
        );
        Seq<String> input = Seq.from(values);

        // Act
        Seq<String> result = input.take(1337);

        // Assert
        assertEquals(values, result.toList().eval());
    }

}
