package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Seq#concatWith}.
 */
@SuppressWarnings("CodeBlock2Expr")
public final class Seq_ConcatWithTests {

    @Test
    public void returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Seq<Integer> seq = Seq.<Integer>empty().concatWith(Seq.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void returnsSingleSequence_whenConcattingWithEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = input.concatWith(Seq.empty());

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList().tryEval());
    }

    @Test
    public void returnsConcatenation_whenConcattingTwoSequences() throws InterruptedException {
        // Arrange
        Seq<Integer> input1 = Seq.of(1, 2, 3);
        Seq<Integer> input2 = Seq.of(4, 5);

        // Act
        Seq<Integer> seq = input1.concatWith(input2);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), seq.toList().tryEval());
    }

    @Test
    public void returnsSequenceTwice_whenConcattingWithItself() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = input.concatWith(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3), seq.toList().tryEval());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Seq<String> sequence = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.<String>empty().concatWith(sequence);
        });
    }

}
