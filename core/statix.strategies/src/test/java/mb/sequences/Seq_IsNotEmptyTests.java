package mb.sequences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#isNotEmpty}.
 */
@SuppressWarnings("ConstantConditions")
public final class Seq_IsNotEmptyTests {
    @Test
    public void returnsFalse_whenTheSequenceIsEmpty() throws InterruptedException {
        // Arrange
        Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.isNotEmpty();

        // Act
        boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsNotEmpty() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.isNotEmpty();

        // Act
        boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

}
