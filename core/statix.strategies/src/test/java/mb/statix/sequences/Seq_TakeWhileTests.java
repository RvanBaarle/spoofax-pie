package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#takeWhile}.
 */
public final class Seq_TakeWhileTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.takeWhile(it -> it < 3);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(0, 1, 2), result.take(4).toList().eval());
        assertEquals(4, i.get());
    }

    @Test
    public void shouldTakeTheFirstElementsThatMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b",
            "c", "d", "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.takeWhile(it -> it.toCharArray()[0] < 'c');

        // Assert
        assertEquals(Arrays.asList(
            "a", "b"
        ), result.toList().eval());
    }


    @Test
    public void shouldTakeNoElements_whenNoElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.takeWhile(it -> false);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldTakeAllElements_whenAllElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        final List<String> values = Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        );
        Seq<String> input = Seq.from(values);

        // Act
        Seq<String> result = input.takeWhile(it -> true);

        // Assert
        assertEquals(values, result.toList().eval());
    }

}
