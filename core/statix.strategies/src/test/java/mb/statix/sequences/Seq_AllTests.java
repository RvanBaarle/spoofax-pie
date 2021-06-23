package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#all}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Seq_AllTests {

    @Test
    public void returnsTrue_whenTheSequenceIsEmpty() throws InterruptedException {
        // Arrange
        final Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.all(it -> { throw new IllegalStateException(); });

        // Act
        final boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsTrueForAllElements() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(2, 4, 6);
        final Computation<Boolean> sut = seq.all(it -> it % 2 == 0);

        // Act
        boolean result = sut.eval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsFalseForAnyElements() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.all(it -> it % 2 == 0);

        // Act
        boolean result = sut.eval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        Seq<String> seq = Seq.of("x");
        final Computation<Boolean> sut = seq.all(it -> { throw new IllegalStateException(); });

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
            seq.all(predicate);
        });
    }

}
