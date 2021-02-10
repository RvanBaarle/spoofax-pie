package mb.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Sequence#from}.
 */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "CodeBlock2Expr", "ConstantConditions"})
public final class Sequence_FromTests {

    @Test
    public void returnsAnEmptySequence_whenGivenAnEmptyIterable() {
        // Arrange
        Iterable<String> iterable = Collections.emptySet();

        // Act
        Sequence<String> seq = Sequence.from(iterable);

        // Assert
        assertEquals(Arrays.asList(), seq.toList());
    }

    @Test
    public void returnsASingletonSequence_whenGivenASingletonIterable() {
        // Arrange
        Iterable<String> iterable = Collections.singleton("a");

        // Act
        Sequence<String> seq = Sequence.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a"), seq.toList());
    }

    @Test
    public void returnsASequenceWithTheGivenElements_whenGivenAnIterable() {
        // Arrange
        Iterable<String> iterable = Arrays.asList("a", "b", "c");

        // Act
        Sequence<String> seq = Sequence.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenCoercedMultipleTimes() {
        // Arrange
        AtomicInteger n = new AtomicInteger();
        Iterable<String> list = Arrays.asList("a", "b", "c");
        Iterable<String> iterable = () -> {
            n.incrementAndGet();
            return list.iterator();
        };

        // Act
        Sequence<String> seq = Sequence.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());
        assertEquals(3, n.get());
    }

    @Test
    public void returnsADifferenceSequence_whenTheOriginalIterableIsChanged() {
        // Act
        List<String> list = Arrays.asList("a", "b", "c");
        Sequence<String> seq = Sequence.from(list);

        // Act & Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList());

        list.set(0, "A");
        list.set(1, "B");
        list.set(2, "C");

        assertEquals(Arrays.asList("A", "B", "C"), seq.toList());
    }

    @Test
    public void returnsAsManyValuesAsRequested_whenTheIterableIsInfinite() {
        // Arrange
        AtomicInteger hasNextEvaluated = new AtomicInteger(0);
        AtomicInteger nextEvaluated = new AtomicInteger(0);
        Iterable<Integer> itr = () -> new Iterator<Integer>() {
            private int value = 0;

            @Override
            public boolean hasNext() {
                hasNextEvaluated.incrementAndGet();
                return true;
            }

            @Override
            public Integer next() {
                nextEvaluated.incrementAndGet();
                this.value += 1;
                return this.value;
            }
        };
        Sequence<Integer> seq = Sequence.from(itr);

        // Assert
        assertEquals(0, hasNextEvaluated.get());
        assertEquals(0, nextEvaluated.get());

        // Act
        final List<Integer> values = new ArrayList<>(10);
        final Iterator<Integer> iterator = seq.iterator();
        for (int i = 0; i < 10; i++) {
            assertTrue(iterator.hasNext());
            values.add(iterator.next());
        }

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
        assertEquals(10, hasNextEvaluated.get());
        assertEquals(10, nextEvaluated.get());
    }


    @Test
    public void returnsASequenceWithNullElements_whenGivenNullElements() {
        // Arrange
        List<String> list = Arrays.asList("a", null, "c");

        // Act
        Sequence<String> seq = Sequence.from(list);

        // Assert
        assertEquals(Arrays.asList("a", null, "c"), seq.toList());
    }

    @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Iterable<String> values = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Sequence.from(values);
        });
    }

}
