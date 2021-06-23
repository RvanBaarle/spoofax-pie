package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#dropWhile}.
 */
public final class Seq_DropWhileTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.dropWhile(it -> it < 3);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(3, 4, 5, 6), result.take(4).toList().eval());
        assertEquals(7, i.get());
    }

    @Test
    public void shouldDropTheFirstElementsThatMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b",
            "c", "d", "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.dropWhile(it -> it.toCharArray()[0] < 'c');

        // Assert
        assertEquals(Arrays.asList(
            "c", "d", "a", "b", "c", "d", "a", "b"
        ), result.toList().eval());
    }


    @Test
    public void shouldDropAllElements_whenAllElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.from(Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        ));

        // Act
        Seq<String> result = input.dropWhile(it -> true);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldDropNoElements_whenNoElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        final List<String> values = Arrays.asList(
            "a", "b", "c", "d",
            "a", "b", "c", "d", "a", "b"
        );
        Seq<String> input = Seq.from(values);

        // Act
        Seq<String> result = input.dropWhile(it -> false);

        // Assert
        assertEquals(values, result.toList().eval());
    }

}
