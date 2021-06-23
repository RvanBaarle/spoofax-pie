package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#map}.
 */
public final class Seq_MapTests {

    @Test
    public void returnsEmptySequence_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.empty();

        // Act
        Seq<Integer> seq = input.map(it -> it + 10);

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void doesNotInvokeMappingFunction_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.empty();

        // Act
        Seq<Integer> seq = input.map(it -> {
            throw new AssertionError("Mapping function invoked!");
        });

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void appliesMappingFunction_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = input.map(it -> it + 10);

        // Assert
        assertEquals(Arrays.asList(11, 12, 13), seq.toList().tryEval());
    }

    @Test
    public void appliesMappingFunctionOnlyToRequestedValues_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        AtomicInteger evaluated = new AtomicInteger(0);
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = input.map(it -> { evaluated.incrementAndGet(); return it + 10; });
        final InterruptibleIterator<Integer> iterator = seq.iterator();
        assertTrue(iterator.hasNext());
        Integer value = iterator.next();

        // Assert
        assertEquals(11, (int)value);
        assertEquals(1, evaluated.get());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable InterruptibleFunction<String, Integer> transform = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.<String>empty().map(transform);
        });
    }

}
