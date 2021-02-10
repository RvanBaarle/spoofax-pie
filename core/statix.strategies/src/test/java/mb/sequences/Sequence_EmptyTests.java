package mb.sequences;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Sequence#empty}.
 */
public final class Sequence_EmptyTests {

    @Test
    public void returnsAnEmptySequence() {
        // Act
        Sequence<String> seq = Sequence.empty();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void returnsAnEmptySequenceEveryTime_whenCoercedMultipleTimes() {
        // Act
        Sequence<String> seq = Sequence.empty();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
        assertEquals(Collections.emptyList(), seq.toList());
        assertEquals(Collections.emptyList(), seq.toList());
    }

}
