package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#any}.
 */
public final class Sequence_AnyTests {
    @Test
    public void returnsFalse_whenTheSequenceIsEmpty_1() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.any();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsNotEmpty() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.any();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsEmpty_2() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.any(it -> { throw new IllegalStateException(); });

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsTrueForAnyElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.any(it -> it % 2 == 0);

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsFalseForAllElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 3, 5);

        // Act
        boolean result = seq.any(it -> it % 2 == 0);

        // Assert
        assertFalse(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        Sequence<String> seq = Sequence.of("x");

        // Act
        assertThrows(IllegalStateException.class, () -> {
            seq.any(it -> { throw new IllegalStateException(); });
        });
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Arrange
        Sequence<String> seq = Sequence.empty();
        @Nullable Predicate<String> predicate = null;

        // Act
        assertThrows(NullPointerException.class, () -> {
            seq.any(predicate);
        });
    }

}
