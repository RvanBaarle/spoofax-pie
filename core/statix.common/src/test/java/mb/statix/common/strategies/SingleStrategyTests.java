package mb.statix.common.strategies;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the {@code SingleStrategy} class.
 */
public final class SingleStrategyTests {

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
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

    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
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

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        final ExceptionStrategy<Object, Object> s = new ExceptionStrategy<>(() -> new IllegalStateException("Evaluated!"));
        final SingleStrategy<Object, Object, Void> sut = new SingleStrategy<>(s);

        // Act
        final Sequence<Void> seq = sut.apply(new Object(), new Object());

        // Assert
        assertThrows(IllegalStateException.class, () -> {
            seq.toList();
        });
    }

    @Test
    public void shouldNotEvaluateBeyondWhatIsNeeded() throws InterruptedException {
        // Arrange
        final AtomicInteger i = new AtomicInteger();
        final Strategy<Object, Object, Integer> s = (o, input) -> {
            // Sequence that counts the number of invocations
            final int val = i.incrementAndGet();
            if (val > 10) throw new InterruptedException("Safeguard; too many invocations.");
            return Sequence.of(val);
        };
        final SingleStrategy<Object, Object, Integer> sut = new SingleStrategy<>(s);

        // Act
        final List<Integer> results = sut.apply(new Object(), new Object()).toList();

        // Assert
        assertEquals(Collections.emptyList(), results);
        assertEquals(1, i.get());
    }

}
