package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Seq#any}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Seq_AnyTests {
    @Test
    public void returnsFalse_whenTheSequenceIsEmpty_1() throws InterruptedException {
        // Arrange
        final Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.any();

        // Act
        boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsNotEmpty() throws InterruptedException {
        // Arrange
        final Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.any();

        // Act
        boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsEmpty_2() throws InterruptedException {
        // Arrange
        final Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.any(it -> { throw new IllegalStateException(); });

        // Act
        boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsTrueForAnyElements() throws InterruptedException {
        // Arrange
        final Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.any(it -> it % 2 == 0);

        // Act
        boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsFalseForAllElements() throws InterruptedException {
        // Arrange
        final Seq<Integer> seq = Seq.of(1, 3, 5);
        final Computation<Boolean> sut = seq.any(it -> it % 2 == 0);

        // Act
        boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        final Seq<String> seq = Seq.of("x");
        final Computation<Boolean> sut = seq.any(it -> { throw new IllegalStateException(); });

        // Act
        assertThrows(IllegalStateException.class, () -> {
            sut.tryEval();
        });
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Arrange
        final Seq<String> seq = Seq.empty();
        final @Nullable Predicate<String> predicate = null;

        // Act
        assertThrows(NullPointerException.class, () -> {
            seq.any(predicate);
        });
    }

}
