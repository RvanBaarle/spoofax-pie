package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Seq#single}.
 */
public final class Seq_SingleTests {

    @Test
    public void returnsFailedComputation_whenEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.empty();

        // Act
        Computation<Integer> seq = input.single();

        // Assert
        assertNull(seq.tryEval());
    }

    @Test
    public void returnsFailedComputation_whenSequenceIsNotSingleton() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Computation<Integer> seq = input.single();

        // Assert
        assertNull(seq.tryEval());
    }

    @Test
    public void returnsSingleResult_whenSequenceIsSingleton() throws InterruptedException {
        // Arrange
        Seq<String> input = Seq.of("X");

        // Act
        Computation<String> seq = input.single();

        // Assert
        assertEquals("X", seq.tryEval());
    }

    @Test
    public void doesNotEvaluateMoreThanNeeded() throws InterruptedException {
        // Arrange
        // Infinite sequence counting from 1
        AtomicInteger counter = new AtomicInteger(0);
        Seq<Integer> input = Seq.from(counter::incrementAndGet);

        // Act
        @Nullable Integer result = input.single().tryEval();

        // Assert
        assertNull(result);
        assertEquals(2, counter.get());
    }

    @Test
    public void returnsFailedComputation_whenNothingMatchesPredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Computation<Integer> seq = input.single(it -> false);

        // Assert
        assertNull(seq.tryEval());
    }

    @Test
    public void returnsFailedComputation_whenMoreThanOneMatchesPredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Computation<Integer> seq = input.single(it -> it > 1);

        // Assert
        assertNull(seq.tryEval());
    }

    @Test
    public void returnsSingleResult_whenExactlyOneMatchesPredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Computation<Integer> seq = input.single(it -> it == 2);

        // Assert
        assertEquals(2, seq.tryEval());
    }

    @Test
    public void doesNotEvaluateMoreThanNeeded_whenMatchingPredicate() throws InterruptedException {
        // Arrange
        // Infinite sequence counting from 1
        AtomicInteger counter = new AtomicInteger(0);
        Seq<Integer> input = Seq.from(counter::incrementAndGet);

        // Act
        @Nullable Integer result = input.single(it -> true).tryEval();

        // Assert
        assertNull(result);
        assertEquals(2, counter.get());
    }

}
