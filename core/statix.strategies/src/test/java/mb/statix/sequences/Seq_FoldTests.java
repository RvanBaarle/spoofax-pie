package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#fold}.
 */
public final class Seq_FoldTests {

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Seq<Integer> result = input.take(10).fold(42, Integer::sum);

        // Assert
        assertEquals(0, i.get());
        assertEquals(Arrays.asList(87), result.toList().eval());
        assertEquals(10, i.get());
    }

    @Test
    public void shouldFold() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5));

        // Act
        Seq<Integer> result = input.fold(42, Integer::sum);

        // Assert
        assertEquals(Arrays.asList(66), result.toList().eval());
    }

    @Test
    public void shouldOnlyReturnInitialValue_whenSequenceIsEmpty() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of();

        // Act
        Seq<Integer> result = input.fold(42, Integer::sum);

        // Assert
        assertEquals(Arrays.asList(42), result.toList().eval());
    }

}
