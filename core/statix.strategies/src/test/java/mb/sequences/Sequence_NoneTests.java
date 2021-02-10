package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#none}.
 */
public final class Sequence_NoneTests {
    @Test
    public void returnsTrue_whenTheSequenceIsEmpty_1() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.none();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsNotEmpty() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.none();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsEmpty_2() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.none(it -> { throw new IllegalStateException(); });

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsTrueForAnyElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.none(it -> it % 2 == 0);

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsFalseForAllElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 3, 5);

        // Act
        boolean result = seq.none(it -> it % 2 == 0);

        // Assert
        assertTrue(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        Sequence<String> seq = Sequence.of("x");

        // Act
        assertThrows(IllegalStateException.class, () -> {
            seq.none(it -> { throw new IllegalStateException(); });
        });
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Arrange
        Sequence<String> seq = Sequence.empty();
        @Nullable Predicate<String> predicate = null;

        // Act
        assertThrows(NullPointerException.class, () -> {
            seq.none(predicate);
        });
    }

}
