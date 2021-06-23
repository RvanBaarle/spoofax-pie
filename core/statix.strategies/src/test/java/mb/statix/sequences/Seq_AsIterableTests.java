package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#asIterable}.
 */
public final class Seq_AsIterableTests {

    @Test
    public void shouldReturnSequenceAsIterable() throws InterruptedException {
        // Arrange
        List<Integer> values = Arrays.asList(0, 1, 2, 3, 4, 5);
        Seq<Integer> input = Seq.from(values);

        // Act
        Iterable<Integer> iterable = input.asIterable();

        // Assert
        ArrayList<Integer> results = new ArrayList<>();
        iterable.forEach(results::add);
        assertEquals(values, results);
    }

}
