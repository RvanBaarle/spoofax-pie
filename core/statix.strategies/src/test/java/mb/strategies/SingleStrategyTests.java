package mb.strategies;

import mb.sequences.InterruptibleIterator;
import mb.sequences.Seq;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static mb.strategies.Strategies.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests the {@code SingleStrategy} class.
 */
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public final class SingleStrategyTests {

    private final String name = "single";

    @Test
    public void isNamed() throws IOException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, String>, Object, String> strategy = SingleStrategy.getInstance();
        final StringBuilder sb = new StringBuilder();

        // Act
        final StringBuilder sb2 = strategy.write(sb);

        // Assert
        assertEquals(name, sb.toString());
        assertEquals(name, strategy.toString());
        assertEquals(name, strategy.getName());
        assertFalse(strategy.isAnonymous());
        assertSame(sb, sb2);
    }

    @Test
    public void isAnonymous_whenApplied() throws IOException {
        // Arrange
        final Strategy1<Object, Strategy<Object, String, String>, String, String> strategy = SingleStrategy.getInstance();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy<Object, String, String> appl = strategy.apply(Strategies.id());
        final StringBuilder sb2 = appl.write(sb);

        // Assert
        assertEquals(name + "(id)", sb.toString());
        assertEquals(name + "(id)", appl.toString());
        assertTrue(appl.isAnonymous());
        assertSame(sb, sb2);
    }

    @Test
    public void shouldReturnNothing_whenSourceIsEmpty() throws InterruptedException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, String>, Object, String> strategy = SingleStrategy.getInstance();
        final List<String> input = Arrays.asList();
        final Strategy<Object, Object, String> appl = strategy.apply(build(input));

        // Act
        final List<String> results = appl.eval(new Object(), new Object()).toList().tryEval();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnSingleValue_whenSourceHasSingleValue() throws InterruptedException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, String>, Object, String> strategy = SingleStrategy.getInstance();
        final List<String> input = Arrays.asList("a");
        final Strategy<Object, Object, String> appl = strategy.apply(build(input));

        // Act
        final List<String> results = appl.eval(new Object(), new Object()).toList().tryEval();

        // Assert
        assertEquals(input, results);
    }

    @Test
    public void shouldReturnNothing_whenSourceHasMultipleValues() throws InterruptedException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, String>, Object, String> strategy = SingleStrategy.getInstance();
        final List<String> input = Arrays.asList("a", "b", "c");
        final Strategy<Object, Object, String> appl = strategy.apply(build(input));

        // Act
        final List<String> results = appl.eval(new Object(), new Object()).toList().tryEval();

        // Assert
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    @Disabled("Both implementation and test are wrong")
    public void shouldNotEvaluate_whenNotCoerced() throws InterruptedException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, String>, Object, String> strategy = SingleStrategy.getInstance();
        AtomicBoolean evaluated = new AtomicBoolean(false);
        // FIXME: This should be an infinite sequence
        final Strategy<Object, Object, String> s = (o, input) -> (Seq<String>)() -> {
            evaluated.set(true);
            return InterruptibleIterator.empty();
        };
        final Strategy<Object, Object, String> appl = strategy.apply(s);

        // Act
        appl.eval(new Object(), new Object());

        // Assert
        assertFalse(evaluated.get());
    }

    @Test
    @Disabled("Both implementation and test are wrong")
    public void shouldNotEvaluateBeyondWhatIsNeeded() throws InterruptedException {
        // Arrange
        final Strategy1<Object, Strategy<Object, Object, Integer>, Object, Integer> strategy = SingleStrategy.getInstance();
        final AtomicInteger i = new AtomicInteger();
        // FIXME: This should be an infinite sequence
        final Strategy<Object, Object, Integer> s = (o, input) -> (Seq<Integer>)() -> {
            // Sequence that counts the number of invocations
            final int val = i.incrementAndGet();
            return InterruptibleIterator.wrap(Collections.singleton(val).iterator());
        };
        final Strategy<Object, Object, Integer> appl = strategy.apply(s);

        // Act
        final List<Integer> results = appl.eval(new Object(), new Object()).toList().tryEval();

        // Assert
        assertEquals(Arrays.asList(), results);
        assertEquals(1, i.get());
    }

}
