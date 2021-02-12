package mb.sequences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Seq#isEmpty}.
 */
@SuppressWarnings("ConstantConditions")
public final class Seq_IsEmptyTests {
    @Test
    public void returnsTrue_whenTheSequenceIsEmpty() throws InterruptedException {
        // Arrange
        final Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.isEmpty();

        // Act
        final boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsNotEmpty() throws InterruptedException {
        // Arrange
        final Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.isEmpty();

        // Act
        final boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

}
