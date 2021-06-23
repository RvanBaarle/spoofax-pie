package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#filter}.
 */
public final class Seq_FilterTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.filter(it -> it % 2 == 0);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(0, 2, 4, 6), result.take(4).toList().eval());
        assertEquals(7, i.get());
    }

    @Test
    public void shouldReturnOnlyElementsThatMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5));

        // Act
        Seq<Integer> result = input.filter(it -> it % 2 == 0);

        // Assert
        assertEquals(Arrays.asList(
            0, 2, 4, 2
        ), result.toList().eval());
    }


    @Test
    public void shouldReturnNoElements_whenNoElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5));

        // Act
        Seq<Integer> result = input.filter(it -> false);

        // Assert
        assertEquals(Collections.emptyList(), result.toList().eval());
    }

    @Test
    public void shouldReturnAllElements_whenAllElementsMatchThePredicate() throws InterruptedException {
        // Arrange
        final List<Integer> values = Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5);
        Seq<Integer> input = Seq.from(values);

        // Act
        Seq<Integer> result = input.filter(it -> true);

        // Assert
        assertEquals(values, result.toList().eval());
    }

}
