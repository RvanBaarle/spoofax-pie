package mb.sequences;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#empty}.
 */
public final class Seq_EmptyTests {

    @Test
    public void returnsAnEmptySequence() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.empty();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void returnsAnEmptySequenceEveryTime_whenCoercedMultipleTimes() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.empty();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

}
