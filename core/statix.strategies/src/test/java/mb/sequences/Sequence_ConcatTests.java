package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Sequence#concat}.
 */
public final class Sequence_ConcatTests {

    @Test
    public void returnsEmptySequence_whenConcattingNoSequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.concat();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.concat(Sequence.empty(), Sequence.empty(), Sequence.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void returnsSingleSequence_whenConcattingASingleSequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = Sequence.concat(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList());
    }

    @Test
    public void returnsConcatenation_whenConcattingMultipleSequences() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.empty();
        Sequence<Integer> input3 = Sequence.of(4, 5);
        Sequence<Integer> input4 = Sequence.of(6);

        // Act
        Sequence<Integer> seq = Sequence.concat(input1, input2, input3, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), seq.toList());
    }

    @Test
    public void returnsElementsMultipleTimes_whenConcattingSequencesMultipleTimes() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.<Integer>empty();
        Sequence<Integer> input3 = Sequence.of(4, 5);
        Sequence<Integer> input4 = Sequence.of(6);

        // Act
        Sequence<Integer> seq = Sequence.concat(input1, input2, input3, input1, input2, input3, input4, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 6), seq.toList());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Sequence<String>[] sequences = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Sequence.concat(sequences);
        });
    }

}
