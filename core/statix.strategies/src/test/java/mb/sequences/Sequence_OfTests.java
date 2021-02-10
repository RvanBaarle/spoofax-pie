package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Sequence#of}.
 */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "CodeBlock2Expr", "ConstantConditions"})
public final class Sequence_OfTests {

    @Test
    public void returnsAnEmptySequence_whenGivenNoElements() {
        // Act
        Sequence<String> seq = Sequence.of();

        // Assert
        assertEquals(Arrays.asList(), seq.toList());
    }

    @Test
    public void returnsASingletonSequence_whenGivenOneElement() {
        // Act
        Sequence<String> seq = Sequence.of("a");

        // Assert
        assertEquals(Arrays.asList("a"), seq.toList());
    }

    @Test
    public void returnsASequenceWithTheGivenElements_whenGivenElements() {
        // Act
        Sequence<String> seq = Sequence.of("a", "b", "c");

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenCoercedMultipleTimes() {
        // Act
        Sequence<String> seq = Sequence.of("a", "b", "c");

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenTheOriginalArrayIsChanged() {
        // Act
        String[] values = new String[]{"a", "b", "c"};
        Sequence<String> seq = Sequence.of(values);

        // Act & Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());

        values[0] = "A";
        values[1] = "B";
        values[2] = "C";

        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
    }

    @Test
    public void returnsASequenceWithNullElements_whenGivenNullElements() {
        // Act
        Sequence<String> seq = Sequence.of("a", null, "c");

        // Assert
        assertEquals(Arrays.asList("a", null, "c"), seq.toList());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable String[] values = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Sequence.of(values);
        });
    }

}
