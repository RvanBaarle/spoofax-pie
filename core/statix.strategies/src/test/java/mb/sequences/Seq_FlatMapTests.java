package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Seq#flatMap}.
 */
@SuppressWarnings("CodeBlock2Expr")
public final class Seq_FlatMapTests {

    @Test
    public void returnsEmptySequence_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.empty();

        // Act
        Seq<Integer> seq = input.flatMap(it -> Seq.of(it + 10, it + 100, it + 1000));

        // Assert
        assertEquals(Collections.emptyList(), seq.toList().tryEval());
    }

    @Test
    public void doesNotInvokeMappingFunction_whenMappingOverEmptySequence() throws InterruptedException {
        // Arrange
        Seq<Integer> input = Seq.empty();

        // Act
        Seq<Integer> seq = input.flatMap(it -> {
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
        Seq<Integer> seq = input.flatMap(it -> Seq.of(it + 10, it + 100, it + 1000));

        // Assert
        assertEquals(Arrays.asList(11, 101, 1001, 12, 102, 1002, 13, 103, 1003), seq.toList().tryEval());
    }

    @Test
    public void appliesMappingFunctionOnlyToRequestedValues_whenMappingOverSequence() throws InterruptedException {
        // Arrange
        AtomicInteger evaluated = new AtomicInteger(0);
        Seq<Integer> input = Seq.of(1, 2, 3);

        // Act
        Seq<Integer> seq = input.flatMap(it -> { evaluated.incrementAndGet(); return Seq.of(it + 10, it + 100, it + 1000); });
        final InterruptibleIterator<Integer> iterator = seq.iterator();

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
        @Nullable InterruptibleFunction<String, Seq<String>> transform = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.<String>empty().flatMap(transform);
        });
    }

}
