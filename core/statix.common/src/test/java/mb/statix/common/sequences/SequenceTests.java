package mb.statix.common.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.spoofax.terms.util.Assert.assertEquals;

/**
 * Tests the {@link Sequence} class.
 */
@SuppressWarnings({"ArraysAsListWithZeroOrOneArgument", "Convert2Diamond"})
public final class SequenceTests {

    @Test
    public void empty_returnsAnEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<String> seq = Sequence.empty();

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Collections.emptyList(), values);
    }


    @Test
    public void of_returnsSequenceWithTheSpecifiedValues_whenGivenAnArrayOfValues() throws InterruptedException {
        // Arrange
        Sequence<String> seq = Sequence.of("a", "b", "c");

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), values);
    }

    @Test
    public void of_returnsSequenceWithTheSpecifiedValue_whenGivenASingletonValue() throws InterruptedException {
        // Arrange
        Sequence<String> seq = Sequence.of("a");

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Arrays.asList("a"), values);
    }

    @Test
    public void of_returnsAnEmptySequence_whenGivenNoValues() throws InterruptedException {
        // Arrange
        Sequence<String> seq = Sequence.of();

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Collections.emptyList(), values);
    }

    @Test
    public void of_returnsTheOriginalValues_whenTheGivenArrayIsChanged() throws InterruptedException {
        // Arrange
        String[] inputs = new String[]{"a", "b", "c"};
        Sequence<String> seq = Sequence.of(inputs);

        // Act
        inputs[0] = inputs[0].toUpperCase(Locale.ROOT);
        inputs[1] = inputs[1].toUpperCase(Locale.ROOT);
        inputs[2] = inputs[2].toUpperCase(Locale.ROOT);
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Arrays.asList("a", "b", "c"), values);
        assertEquals(Arrays.asList("A", "B", "C"), Arrays.asList(inputs));
    }


    @Test
    public void from_returnsAnEmptySequence_whenTheIterableIsEmpty() throws InterruptedException {
        // Arrange
        AtomicInteger hasNextEvaluated = new AtomicInteger(0);
        AtomicInteger nextEvaluated = new AtomicInteger(0);
        Iterable<String> itr = () -> new Iterator<String>() {
            @Override
            public boolean hasNext() {
                hasNextEvaluated.incrementAndGet();
                return false;
            }

            @Override
            public String next() {
                nextEvaluated.incrementAndGet();
                throw new NoSuchElementException();
            }
        };
        Sequence<String> seq = Sequence.from(itr);

        // Assert
        assertEquals(0, hasNextEvaluated.get());
        assertEquals(0, nextEvaluated.get());

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Collections.emptyList(), values);
        assertEquals(1, hasNextEvaluated.get());
        assertEquals(0, nextEvaluated.get());
    }

    @Test
    public void from_returnsASingletonSequence_whenTheIterableIsSingle() throws InterruptedException {
        // Arrange
        AtomicInteger hasNextEvaluated = new AtomicInteger(0);
        AtomicInteger nextEvaluated = new AtomicInteger(0);
        Iterable<String> itr = () -> new Iterator<String>() {
            @Nullable private String value = "A";

            @Override
            public boolean hasNext() {
                hasNextEvaluated.incrementAndGet();
                return this.value != null;
            }

            @Override
            public String next() {
                nextEvaluated.incrementAndGet();
                if (value == null) throw new NoSuchElementException();
                final String v = this.value;
                this.value = null;
                return v;
            }
        };
        Sequence<String> seq = Sequence.from(itr);

        // Assert
        assertEquals(0, hasNextEvaluated.get());
        assertEquals(0, nextEvaluated.get());

        // Act
        final List<String> values = seq.toList();

        // Assert
        assertEquals(Arrays.asList("A"), values);
        assertEquals(2, hasNextEvaluated.get());
        assertEquals(1, nextEvaluated.get());
    }

    @Test
    public void from_returnsAsManyValuesAsRequested_whenTheIterableIsInfinite() throws InterruptedException {
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
        for (int i = 0; i < 10; i++) {
            seq.tryAdvance(values::add);
        }


        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), values);
        assertEquals(10, hasNextEvaluated.get());
        assertEquals(10, nextEvaluated.get());
    }



    @Test
    public void concat_returnsEmptySequence_whenConcattingNoSequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.concat();

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void concat_returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.concat(Sequence.empty(), Sequence.empty(), Sequence.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void concat_returnsSingleSequence_whenConcattingASingleSequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = Sequence.concat(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList());
    }

    @Test
    public void concat_returnsConcatenation_whenConcattingMultipleSequences() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.empty();
        Sequence<Integer> input3 = Sequence.of(4, 5);
        Sequence<Integer> input4 = Sequence.of(6);

        // Act
        Sequence<Integer> seq = Sequence.concat(input1, input2, input3, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), seq.toList());
    }

    @Test
    public void concat_evaluatesEachSequenceOnce_whenConcattingSequencesMultipleTimes() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.empty();
        Sequence<Integer> input3 = Sequence.of(4, 5);
        Sequence<Integer> input4 = Sequence.of(6);

        // Act
        Sequence<Integer> seq = Sequence.concat(input1, input2, input3, input1, input2, input3, input4, input4);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), seq.toList());
    }


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
        AtomicReference<Integer> value = new AtomicReference<>();

        // Act
        Sequence<Integer> seq = input.map(it -> { evaluated.incrementAndGet(); return it + 10; });
        assertTrue(seq.tryAdvance(value::set));

        // Assert
        assertEquals(11, (int)value.get());
        assertEquals(1, evaluated.get());
    }


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

        // Assert
        AtomicReference<Integer> value = new AtomicReference<>();
        assertTrue(seq.tryAdvance(value::set));
        assertEquals(11, (int)value.get());
        assertTrue(seq.tryAdvance(value::set));
        assertEquals(101, (int)value.get());
        assertTrue(seq.tryAdvance(value::set));
        assertEquals(1001, (int)value.get());
        assertTrue(seq.tryAdvance(value::set));
        assertEquals(12, (int)value.get());
        assertEquals(2, evaluated.get());
    }


    @Test
    public void concatWith_returnsEmptySequence_whenConcattingEmptySequences() throws InterruptedException {
        // Act
        Sequence<Integer> seq = Sequence.<Integer>empty().concatWith(Sequence.empty());

        // Assert
        assertEquals(Collections.emptyList(), seq.toList());
    }

    @Test
    public void concatWith_returnsSingleSequence_whenConcattingWithEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.concatWith(Sequence.empty());

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList());
    }

    @Test
    public void concatWith_returnsConcatenation_whenConcattingTwoSequences() throws InterruptedException {
        // Arrange
        Sequence<Integer> input1 = Sequence.of(1, 2, 3);
        Sequence<Integer> input2 = Sequence.of(4, 5);

        // Act
        Sequence<Integer> seq = input1.concatWith(input2);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), seq.toList());
    }

    @Test
    public void concatWith_evaluatesSequenceOnce_whenConcattingWithItself() throws InterruptedException {
        // Arrange
        Sequence<Integer> input = Sequence.of(1, 2, 3);

        // Act
        Sequence<Integer> seq = input.concatWith(input);

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), seq.toList());
    }


    @Test
    public void toList_returnsEmptyList_whenEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.empty();

        // Act
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Collections.emptyList(), list);
    }

    @Test
    public void toList_returnsAllElements_whenNonEmptySequence() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 3);

        // Act
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void toList_returnsRemainingElements_whenSequenceIsPartiallyEvaluated() throws InterruptedException {
        // Arrange
        Sequence<Integer> seq = Sequence.of(1, 2, 3);

        // Act
        seq.tryAdvance(it -> {});   // Consume the first element
        List<Integer> list = seq.toList();

        // Assert
        assertEquals(Arrays.asList(2, 3), list);
    }
}
