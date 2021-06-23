package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#drop}.
 */
public final class Seq_DropTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.drop(4);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(4, 5, 6, 7), result.take(4).toList().eval());
        assertEquals(8, i.get());
    }

    @Test
    public void shouldDropTheFirstNElements() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.drop(4);

        // Assert
        assertEquals(Arrays.asList(
            "a", "b", "c", "d", "a", "b"
        ), result.toList().eval());
    }


    @Test
    public void shouldDropAllElements_whenNIsLargerThanTheSequence() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.drop(1337);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldDropNoElements_whenNIsZero() throws InterruptedException {
        // Arrange
        final List<String> values = Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        );
        Seq<String> input = Seq.from(values);

        // Act
        Seq<String> result = input.drop(0);

        // Assert
        assertEquals(values, result.toList().eval());
    }

    @Test
    public void shouldDropNoElements_whenNIsNegative() throws InterruptedException {
        // Arrange
        final List<String> values = Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        );
        Seq<String> input = Seq.from(values);

        // Act
        Seq<String> result = input.drop(-1337);

        // Assert
        assertEquals(values, result.toList().eval());
    }

}
