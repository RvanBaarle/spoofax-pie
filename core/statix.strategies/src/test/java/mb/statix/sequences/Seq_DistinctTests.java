package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#distinct}.
 */
public final class Seq_DistinctTests {

    @Test
    public void shouldReturnDistinctElements_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5));

        // Act
        Seq<Integer> distinct = input.distinct();

        // Assert
        assertEquals(new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5)), new HashSet<>(distinct.toList().eval()));
    }

    @Test
    public void shouldKeepOrderOfElements_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 1, 2, 3, 1, 4, 2, 5, 5));

        // Act
        Seq<Integer> distinct = input.distinct();

        // Assert
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), distinct.toList().tryEval());
    }
}
