package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#none}.
 */
@SuppressWarnings({"ConstantConditions", "CodeBlock2Expr", "Convert2MethodRef"})
public final class Seq_NoneTests {
    @Test
    public void returnsTrue_whenTheSequenceIsEmpty_1() throws InterruptedException {
        // Arrange
        Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.none();

        // Act
        boolean result = sut.tryEval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenTheSequenceIsNotEmpty() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.none();

        // Act
        boolean result = sut.tryEval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenTheSequenceIsEmpty_2() throws InterruptedException {
        // Arrange
        Seq<String> seq = Seq.empty();
        final Computation<Boolean> sut = seq.none(it -> { throw new IllegalStateException(); });

        // Act
        boolean result = sut.tryEval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void returnsFalse_whenPredicateIsTrueForAnyElements() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(2, 3, 4);
        final Computation<Boolean> sut = seq.none(it -> it % 2 == 0);

        // Act
        boolean result = sut.tryEval();

        // Assert
        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenPredicateIsFalseForAllElements() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(1, 3, 5);
        final Computation<Boolean> sut = seq.none(it -> it % 2 == 0);

        // Act
        boolean result = sut.tryEval();

        // Assert
        assertTrue(result);
    }

    @Test
    public void throws_whenThePredicateThrows() {
        // Arrange
        Seq<String> seq = Seq.of("x");
        final Computation<Boolean> sut = seq.none(it -> { throw new IllegalStateException(); });

        // Act
        assertThrows(IllegalStateException.class, () -> {
            sut.tryEval();
        });
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Arrange
        Seq<String> seq = Seq.empty();
        @Nullable Predicate<String> predicate = null;

        // Act
        assertThrows(NullPointerException.class, () -> {
            seq.none(predicate);
        });
    }

}
