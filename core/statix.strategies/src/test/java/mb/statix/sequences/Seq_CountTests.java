package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#count}.
 */
public final class Seq_CountTests {

    @Test
    public void computationCountsElementsInSequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 2, 3, 4, 5));

        // Act
        Computation<Integer> computation = input.count();

        // Assert
        assertEquals(6, computation.eval());
    }

    @Test
    public void computationCountsElementsInSequence_whenGivenPredicate() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 2, 3, 4, 5));

        // Act
        Computation<Integer> computation = input.count(i -> i % 2 == 0);

        // Assert
        assertEquals(3, computation.eval());
    }

    @Test
    public void computationOnlyEvaluatesOnceCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement).buffer();

        // Act
        input.count();

        // Assert
        assertEquals(0, i.get());
    }

}
