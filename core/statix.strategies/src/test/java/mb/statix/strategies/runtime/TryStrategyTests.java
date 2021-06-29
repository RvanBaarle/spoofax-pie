package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.Seq;
import mb.statix.strategies.TestListStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link TryStrategy} class.
 */
@SuppressWarnings({"PointlessArithmeticExpression", "ArraysAsListWithZeroOrOneArgument"}) public final class TryStrategyTests {

    @Test
    public void shouldEvaluateToInput_whenStrategyFails() throws InterruptedException {
        // Arrange
        final TryStrategy<Object, Integer> strategy = TryStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s = new TestListStrategy<>(it -> Arrays.asList());

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s, 42);

        // Assert
        assertEquals(Arrays.asList(42), result.toList().eval());
    }

    @Test
    public void shouldEvaluateToResults_whenStrategySucceeds() throws InterruptedException {
        // Arrange
        final TryStrategy<Object, Integer> strategy = TryStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s = new TestListStrategy<>(it -> Arrays.asList(it + 1, it + 2, it + 3));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s, 42);

        // Assert
        assertEquals(Arrays.asList(43, 44, 45), result.toList().eval());
    }

    @Test
    public void shouldEvaluateSequenceLazy() throws InterruptedException {
        // Arrange
        final TryStrategy<Object, Integer> strategy = TryStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s = new TestListStrategy<>(it -> Arrays.asList(it + 1, it + 2, it + 3));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s, 42);
        assertEquals(1, s.evalCalls.get());        // called once to get the lazy sequence

        final InterruptibleIterator<Integer> iterator = result.iterator();
        assertEquals(1, s.iteratorCalls.get());    // called once to get the lazy sequence's iterator

        iterator.next();
        assertEquals(1, s.nextCalls.get());        // called to get the first element

        iterator.next();
        assertEquals(2, s.nextCalls.get());        // called to get the second element

        iterator.next();
        assertEquals(3, s.nextCalls.get());        // called to get the third element

        iterator.hasNext();
        assertEquals(4, s.nextCalls.get());        // called to find the sequence empty

        // Final tally
        assertEquals(1, s.evalCalls.get());
        assertEquals(1, s.iteratorCalls.get());
        assertEquals(4, s.nextCalls.get());
    }

}
