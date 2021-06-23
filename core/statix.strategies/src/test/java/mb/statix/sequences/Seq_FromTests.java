package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Seq#from}.
 */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "CodeBlock2Expr", "ConstantConditions", "Convert2Diamond"})
public final class Seq_FromTests {

    @Test
    public void returnsAnEmptySequence_whenGivenAnEmptyIterable() throws InterruptedException {
        // Arrange
        Iterable<String> iterable = Collections.emptySet();

        // Act
        Seq<String> seq = Seq.from(iterable);

        // Assert
        assertEquals(Arrays.asList(), seq.toList().tryEval());
    }

    @Test
    public void returnsASingletonSequence_whenGivenASingletonIterable() throws InterruptedException {
        // Arrange
        Iterable<String> iterable = Collections.singleton("a");

        // Act
        Seq<String> seq = Seq.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a"), seq.toList().tryEval());
    }

    @Test
    public void returnsASequenceWithTheGivenElements_whenGivenAnIterable() throws InterruptedException {
        // Arrange
        Iterable<String> iterable = Arrays.asList("a", "b", "c");

        // Act
        Seq<String> seq = Seq.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
    }

    @Test
    public void returnsASequenceWithTheGivenElements_whenGivenAStream() throws InterruptedException {
        // Arrange
        Stream<String> stream = Stream.of("a", "b", "c");

        // Act
        Seq<String> seq = Seq.from(stream);

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
    }

    @Test
    public void returnsTheSameSequenceEveryTime_whenCoercedMultipleTimes() throws InterruptedException {
        // Arrange
        AtomicInteger n = new AtomicInteger();
        Iterable<String> list = Arrays.asList("a", "b", "c");
        Iterable<String> iterable = () -> {
            n.incrementAndGet();
            return list.iterator();
        };

        // Act
        Seq<String> seq = Seq.from(iterable);

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());
        assertEquals(3, n.get());
    }

    @Test
    public void returnsADifferenceSequence_whenTheOriginalIterableIsChanged() throws InterruptedException {
        // Act
        List<String> list = Arrays.asList("a", "b", "c");
        Seq<String> seq = Seq.from(list);

        // Act & Assert
        assertEquals(Arrays.asList("a", "b", "c"), seq.toList().tryEval());

        list.set(0, "A");
        list.set(1, "B");
        list.set(2, "C");

        assertEquals(Arrays.asList("A", "B", "C"), seq.toList().tryEval());
    }

    @Test
    public void returnsAsManyValuesAsRequested_whenTheIterableIsInfinite() throws InterruptedException {
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
        Seq<Integer> seq = Seq.from(itr);

        // Assert
        assertEquals(0, hasNextEvaluated.get());
        assertEquals(0, nextEvaluated.get());

        // Act
        final List<Integer> values = new ArrayList<>(10);
        final InterruptibleIterator<Integer> iterator = seq.iterator();
        for (int i = 0; i < 10; i++) {
            assertTrue(iterator.hasNext());
            values.add(iterator.next());
        }

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
        assertEquals(10, hasNextEvaluated.get());
        assertEquals(10, nextEvaluated.get());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") @Test
    public void throws_whenArgumentIsNull() {
        // Act
        @Nullable Iterable<String> values = null;

        // Assert
        assertThrows(NullPointerException.class, () -> {
            Seq.from(values);
        });
    }

}
