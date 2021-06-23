package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#contains}.
 */
public final class Seq_ContainsTests {

    @Test
    public void computationEvaluatesToTrue_whenElementIsInSequence() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        Computation<Boolean> computation = input.contains(4);

        // Assert
        assertTrue(computation.eval());
        assertEquals(5, i.get());
    }

    @Test
    public void computationOnlyEvaluatesWhatIsNeeded_whenElementIsInSequence() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement);

        // Act
        input.contains(4);

        // Assert
        assertEquals(0, i.get());
    }

    @Test
    public void computationEvaluatesToFalse_whenElementIsNotInFiniteSequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.from(Arrays.asList(0, 1, 2, 3, 4, 5));

        // Act
        Computation<Boolean> computation = input.contains(-1);

        // Assert
        assertFalse(computation.eval());
    }

    @Test
    public void computationOnlyEvaluatesOnceCoerced() throws InterruptedException {
        // Arrange
        AtomicInteger i = new AtomicInteger();
        Seq<Integer> input = Seq.from(i::getAndIncrement).buffer();

        // Act
        Computation<Boolean> computation = input.contains(4);

        // Assert
        assertEquals(0, i.get());
        assertTrue(computation.eval());
        assertEquals(5, i.get());
    }

}
