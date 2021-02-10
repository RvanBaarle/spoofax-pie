package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#isEmpty}.
 */
public final class Sequence_IsEmptyTests {
    @Test
    public void returnsTrue_whenTheSequenceIsEmpty() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsNotEmpty() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.isEmpty();

        // Assert
        assertFalse(result);
    }

}
