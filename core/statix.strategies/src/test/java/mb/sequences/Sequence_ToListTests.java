package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#toList}.
 */
public final class Sequence_ToListTests {

    @Test
    public void returnsEmptyList_whenEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.empty();

        // Act
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Collections.emptyList(), list);
    }

    @Test
    public void returnsAllElements_whenNonEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 3);

        // Act
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void returnsAllElements_whenSequenceIsPartiallyEvaluated() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 3);

        // Act
        seq.iterator().next(); // Consume the first element
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

}
