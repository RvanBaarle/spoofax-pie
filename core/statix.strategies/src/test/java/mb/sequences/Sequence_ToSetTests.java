package mb.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Sequence#toSet}.
 */
public final class Sequence_ToSetTests {

    @Test
    public void returnsEmptyList_whenEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.empty();

        // Act
        Set<Integer> list = seq.toSet();

        // Assert
        assertEquals(Collections.emptySet(), list);
    }

    @Test
    public void returnsAllElements_whenNonEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 1, 3, 3);

        // Act
        Set<Integer> list = seq.toSet();

        // Assert
        assertEquals(new LinkedHashSet<>(Arrays.asList(1, 2, 3)), list);
    }

    @Test
    public void returnsAllElements_whenSequenceIsPartiallyEvaluated() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 1, 3, 3);

        // Act
        seq.iterator().next(); // Consume the first element
        Set<Integer> list = seq.toSet();

        // Assert
        assertEquals(new LinkedHashSet<>(Arrays.asList(1, 2, 3)), list);
    }

}
