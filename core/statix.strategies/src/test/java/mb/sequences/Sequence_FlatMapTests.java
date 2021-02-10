package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#flatMap}.
 */
public final class Sequence_FlatMapTests {

    @Test
    public void flatMap_returnsEmptySequence_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.empty();

        // Act
        Sequence<Integer> seq = input.flatMap(it -> Sequence.of(it + 10, it + 100, it + 1000));

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void flatMap_doesNotInvokeMappingFunction_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.empty();

        // Act
        Sequence<Integer> seq = input.flatMap(it -> {
            throw new AssertionError("Mapping function invoked!");
        });

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void flatMap_appliesMappingFunction_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.flatMap(it -> Sequence.of(it + 10, it + 100, it + 1000));

        // Assert
        assertEquals(Arrays.asList(11, 101, 1001, 12, 102, 1002, 13, 103, 1003), seq.toList());
    }

    @Test
    public void flatMap_appliesMappingFunctionOnlyToRequestedValues_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        AtomicInteger evaluated = new AtomicInteger(0);
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.flatMap(it -> { evaluated.incrementAndGet(); return Sequence.of(it + 10, it + 100, it + 1000); });
        final Iterator<Integer> iterator = seq.iterator();

        // Assert
        Integer value;
        assertTrue(iterator.hasNext());
        value = iterator.next();
        assertEquals(11, (int)value);
        assertTrue(iterator.hasNext());
        value = iterator.next();
        assertEquals(101, (int)value);
        assertTrue(iterator.hasNext());
        value = iterator.next();
        assertEquals(1001, (int)value);
        assertTrue(iterator.hasNext());
        value = iterator.next();
        assertEquals(12, (int)value);
        assertEquals(2, evaluated.get());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable InterruptibleFunction<String, Sequence<String>> transform = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Sequence.<String>empty().flatMap(transform);
        });
    }

}
