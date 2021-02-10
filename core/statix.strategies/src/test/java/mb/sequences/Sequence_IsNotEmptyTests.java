package mb.sequences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Sequence#isNotEmpty}.
 */
public final class Sequence_IsNotEmptyTests {
    @Test
    public void returnsFalse_whenTheSequenceIsEmpty() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.isNotEmpty();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsNotEmpty() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.isNotEmpty();

        // Assert
        assertTrue(result);
    }

}
