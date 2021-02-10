package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#map}.
 */
public final class Sequence_MapTests {

    @Test
    public void map_returnsEmptySequence_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.empty();

        // Act
        Sequence<Integer> seq = input.map(it -> it + 10);

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void map_doesNotInvokeMappingFunction_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.empty();

        // Act
        Sequence<Integer> seq = input.map(it -> {
            throw new AssertionError("Mapping function invoked!");
        });

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void map_appliesMappingFunction_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.map(it -> it + 10);

        // Assert
        assertEquals(Arrays.asList(11, 12, 13), seq.toList());
    }

    @Test
    public void map_appliesMappingFunctionOnlyToRequestedValues_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        AtomicInteger evaluated = new AtomicInteger(0);
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.map(it -> { evaluated.incrementAndGet(); return it + 10; });
        final Iterator<Integer> iterator = seq.iterator();
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
            Sequence.<String>empty().map(transform);
        });
    }

}
