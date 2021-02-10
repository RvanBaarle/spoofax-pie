package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#all}.
 */
public final class Sequence_AllTests {

    @Test
    public void returnsTrue_whenTheSequenceIsEmpty() {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        boolean result = seq.all(it -> { throw new IllegalStateException(); });

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsTrueForAllElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 4, 6);

        // Act
        boolean result = seq.all(it -> it % 2 == 0);

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsFalseForAnyElements() {
        // Arrange
        Sequence<Integer> seq = Sequence.of(2, 3, 4);

        // Act
        boolean result = seq.all(it -> it % 2 == 0);

        // Assert
        assertFalse(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        Sequence<String> seq = Sequence.of("x");

        // Act
        assertThrows(IllegalStateException.class, () -> {
            seq.all(it -> { throw new IllegalStateException(); });
        });
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Arrange
        Sequence<String> seq = Sequence.empty();
        @Nullable Predicate<String> predicate = null;

        // Act
        assertThrows(NullPointerException.class, () -> {
            seq.all(predicate);
        });
    }

}
