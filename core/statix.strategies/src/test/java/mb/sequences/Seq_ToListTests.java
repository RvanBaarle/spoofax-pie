package mb.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Seq#toList}.
 */
public final class Seq_ToListTests {

    @Test
    public void returnsEmptyList_whenEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.empty();
        final Computation<List<Integer>> sut = seq.toList();

        // Act
        List<Integer> list = sut.tryEval();

        // Assert
        assertEquals(Collections.emptyList(), list);
    }

    @Test
    public void returnsAllElements_whenNonEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(1, 2, 3);
        final Computation<List<Integer>> sut = seq.toList();

        // Act
        List<Integer> list = sut.tryEval();

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void returnsAllElements_whenSequenceIsPartiallyEvaluated() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(1, 2, 3);
        final Computation<List<Integer>> sut = seq.toList();

        // Act
        seq.iterator().next(); // Consume the first element
        List<Integer> list = sut.tryEval();

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

}
