package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#toSet}.
 */
public final class Seq_ToSetTests {

    @Test
    public void returnsEmptyList_whenEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.empty();
        final Computation<Set<Integer>> sut = seq.toSet();

        // Act
        Set<Integer> list = sut.tryEval();

        // Assert
        assertEquals(Collections.emptySet(), list);
    }

    @Test
    public void returnsAllElements_whenNonEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(1, 2, 1, 3, 3);
        final Computation<Set<Integer>> sut = seq.toSet();

        // Act
        Set<Integer> list = sut.tryEval();

        // Assert
        assertEquals(new LinkedHashSet<>(Arrays.asList(1, 2, 3)), list);
    }

    @Test
    public void returnsAllElements_whenSequenceIsPartiallyEvaluated() throws InterruptedException {
        // Arrange
        Seq<Integer> seq = Seq.of(1, 2, 1, 3, 3);
        final Computation<Set<Integer>> sut = seq.toSet();

        // Act
        seq.iterator().next(); // Consume the first element
        Set<Integer> list = sut.tryEval();

        // Assert
        assertEquals(new LinkedHashSet<>(Arrays.asList(1, 2, 3)), list);
    }

}
