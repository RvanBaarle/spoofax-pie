package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#of}.
 */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "CodeBlock2Expr", "ConstantConditions"})
public final class Seq_OfTests {

    @Test
    public void returnsAnEmptySequence_whenGivenNoElements() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of();

        // Assert
        assertEquals(Arrays.asList(), seq.toList().tryEval());
    }

    @Test
    public void returnsAnEmptySequence_whenGivenAnEmptyArray() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of(new String[0]);

        // Assert
        assertEquals(Arrays.asList(), seq.toList().tryEval());
    }

    @Test
    public void returnsAComputation_whenGivenASingletonArray() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of(new String[] { "a" });

        // Assert
        assertTrue(seq instanceof Computation);
        assertEquals(Arrays.asList("a"), seq.toList().tryEval());
    }

    @Test
    public void returnsASingletonSequence_whenGivenOneElement() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of("a");

        // Assert
        assertEquals(Arrays.asList("a"), seq.toList().tryEval());
    }

    @Test
    public void returnsASequenceWithTheGivenElements_whenGivenElements() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of("a", "b", "c");

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenCoercedMultipleTimes() throws InterruptedException {
        // Act
        Seq<String> seq = Seq.of("a", "b", "c");

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenTheOriginalArrayIsChanged() throws InterruptedException {
        // Act
        String[] values = new String[]{"a", "b", "c"};
        Seq<String> seq = Seq.of(values);

        // Act & Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());

        values[0] = "A";
        values[1] = "B";
        values[2] = "C";

        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable String[] values = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.of(values);
        });
    }

}
