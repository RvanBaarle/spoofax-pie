package mb.statix.common.strategies;

import mb.statix.common.sequences.Sequence;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@code SingleStrategy} class.
 */
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public final class SingleStrategyTests {

    @Test
    public void shouldReturnNothing_whenSourceIsEmpty() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList();
        final BuildStrategy<Object, String> s = new BuildStrategy<>(input);
        final SingleStrategy<Object, Object, String> sut = new SingleStrategy<>(s);

        // Act
        final List<String> results = sut.apply(new Object(), new Object()).toList();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnSingleValue_whenSourceHasSingleValue() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList("a");
        final BuildStrategy<Object, String> s = new BuildStrategy<>(input);
        final SingleStrategy<Object, Object, String> sut = new SingleStrategy<>(s);

        // Act
        final List<String> results = sut.apply(new Object(), new Object()).toList();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnNothing_whenSourceHasMultipleValues() throws InterruptedException {
        // Arrange
        final List<String> input = Arrays.asList("a", "b", "c");
        final BuildStrategy<Object, String> s = new BuildStrategy<>(input);
        final SingleStrategy<Object, Object, String> sut = new SingleStrategy<>(s);

        // Act
        final List<String> results = sut.apply(new Object(), new Object()).toList();

        // Assert
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        AtomicBoolean evaluated = new AtomicBoolean(false);
        final Strategy<Object, Object, Object> s = (o, input) -> (Sequence<Object>)action -> {
            evaluated.set(true);
            return false;
        };
        final SingleStrategy<Object, Object, Object> sut = new SingleStrategy<>(s);

        // Act
        final Sequence<Object> seq = sut.apply(new Object(), new Object());

        // Assert
        assertFalse(evaluated.get());
    }

    @Test
    public void shouldNotEvaluateBeyondWhatIsNeeded() throws InterruptedException {
        // Arrange
        final AtomicInteger i = new AtomicInteger();
        final Strategy<Object, Object, Integer> s = (o, input) -> (Sequence<Integer>)action -> {
            // Sequence that counts the number of invocations
            final int val = i.incrementAndGet();
            action.accept(val);
            return true;
        };
        final SingleStrategy<Object, Object, Integer> sut = new SingleStrategy<>(s);

        // Act
        final List<Integer> results = sut.apply(new Object(), new Object()).toList();

        // Assert
        assertEquals(Collections.emptyList(), results);
        assertEquals(2, i.get());
    }

}
