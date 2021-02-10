package mb.strategies;

import mb.sequences.IteratorBase;
import mb.sequences.Sequence;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static mb.strategies.Strategies.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@code SingleStrategy} class.
 */
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public final class SingleStrategyTests {

    @Test
    public void shouldReturnNothing_whenSourceIsEmpty() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList();
        final Strategy<Object, Object, String> sut = single(build(input));

        // Act
        final List<String> results = sut.eval(new Object(), new Object()).toList();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnSingleValue_whenSourceHasSingleValue() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList("a");
        final Strategy<Object, Object, String> sut = single(build(input));

        // Act
        final List<String> results = sut.eval(new Object(), new Object()).toList();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnNothing_whenSourceHasMultipleValues() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList("a", "b", "c");
        final Strategy<Object, Object, String> sut = single(build(input));

        // Act
        final List<String> results = sut.eval(new Object(), new Object()).toList();

        // Assert
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    @Disabled("Both implementation and test are wrong")
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicBoolean evaluated = new AtomicBoolean(false);
        final Strategy<Object, Object, String> s = (o, input) -> (Sequence<String>)() -> {
            evaluated.set(true);
            return Collections.emptyIterator();
        };
        final Strategy<Object, Object, String> sut = single(s);

        // Act
        final Sequence<String> seq = sut.eval(new Object(), new Object());

        // Assert
        assertFalse(evaluated.get());
    }

    @Test
    @Disabled("Both implementation and test are wrong")
    public void shouldNotEvaluateBeyondWhatIsNeeded() throws InterruptedException {
        // Arrange
        final AtomicInteger i = new AtomicInteger();
        final Strategy<Object, Object, Integer> s = (o, input) -> (Sequence<Integer>)() -> {
            // Sequence that counts the number of invocations
            final int val = i.incrementAndGet();
            return Collections.singleton(val).iterator();
        };
        final Strategy<Object, Object, Integer> sut = single(s);

        // Act
        final List<Integer> results = sut.eval(new Object(), new Object()).toList();

        // Assert
        assertEquals(Arrays.asList(1), results);
        assertEquals(2, i.get());
    }

}
