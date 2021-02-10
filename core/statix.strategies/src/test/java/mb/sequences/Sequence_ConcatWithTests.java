package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Sequence#concatWith}.
 */
public final class Sequence_ConcatWithTests {

    @Test
    public void returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.<Integer>empty().concatWith(Sequence.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void returnsSingleSequence_whenConcattingWithEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.concatWith(Sequence.empty());

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList());
    }

    @Test
    public void returnsConcatenation_whenConcattingTwoSequences() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.of(4, 5);

        // Act
        Sequence<Integer> seq = input1.concatWith(input2);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), seq.toList());
    }

    @Test
    public void returnsSequenceTwice_whenConcattingWithItself() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.concatWith(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 1, 2, 3), seq.toList());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Sequence<String> sequence = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Sequence.<String>empty().concatWith(sequence);
        });
    }

}
