package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#concat}.
 */
public final class Seq_ConcatTests {

    @Test
    public void returnsEmptySequence_whenConcattingNoSequences() throws InterruptedException {
        // Act
        Seq<Integer> seq = Seq.concat();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Seq<Integer> seq = Seq.concat(Seq.empty(), Seq.empty(), Seq.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void returnsSingleSequence_whenConcattingASingleSequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = Seq.concat(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList().tryEval());
    }

    @Test
    public void returnsConcatenation_whenConcattingMultipleSequences() throws InterruptedException {
        // Arrange
        Seq<Integer> input1 = Seq.of(1, 2, 3);
        Seq<Integer> input2 = Seq.empty();
        Seq<Integer> input3 = Seq.of(4, 5);
        Seq<Integer> input4 = Seq.of(6);

        // Act
        Seq<Integer> seq = Seq.concat(input1, input2, input3, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), seq.toList().tryEval());
    }

    @Test
    public void returnsElementsMultipleTimes_whenConcattingSequencesMultipleTimes() throws InterruptedException {
        // Arrange
        Seq<Integer> input1 = Seq.of(1, 2, 3);
        Seq<Integer> input2 = Seq.<Integer>empty();
        Seq<Integer> input3 = Seq.of(4, 5);
        Seq<Integer> input4 = Seq.of(6);

        // Act
        Seq<Integer> seq = Seq.concat(input1, input2, input3, input1, input2, input3, input4, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 6, 6), seq.toList().tryEval());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Seq<String>[] sequences = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.concat(sequences);
        });
    }

}
