package mb.statix.strategies.runtime;

import mb.statix.sequences.Computation;
import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.InterruptibleIteratorBase;
import mb.statix.sequences.Seq;
import mb.statix.strategies.Strategy;
import mb.statix.strategies.TestListStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@link GlcStrategy} class.
 */
public final class GlcStrategyTests {

    @Test
    public void shouldApplyConditionThenThenBranch_whenConditionSucceeds() throws InterruptedException {
        // Arrange
        final GlcStrategy<Object, Integer, Integer, Integer> strategy = GlcStrategy.getInstance();
        final TestListStrategy<Integer, Integer> sc = new TestListStrategy<>(it -> Arrays.asList(it + 0, it + 1, it + 2));
        final TestListStrategy<Integer, Integer> st = new TestListStrategy<>(it -> Arrays.asList(it + 5));
        final TestListStrategy<Integer, Integer> se = new TestListStrategy<>(it -> Arrays.asList(it * 10));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), sc, st, se, 42);

        // Assert
        assertEquals(Arrays.asList(47, 48, 49), result.toList().eval());
    }

    @Test
    public void shouldApplyElseBranch_whenConditionFails() throws InterruptedException {
        // Arrange
        final GlcStrategy<Object, Integer, Integer, Integer> strategy = GlcStrategy.getInstance();
        final TestListStrategy<Integer, Integer> sc = new TestListStrategy<>(it -> Collections.emptyList() /* fail */);
        final TestListStrategy<Integer, Integer> st = new TestListStrategy<>(it -> Arrays.asList(it + 5));
        final TestListStrategy<Integer, Integer> se = new TestListStrategy<>(it -> Arrays.asList(it * 10));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), sc, st, se, 42);

        // Assert
        assertEquals(Arrays.asList(420), result.toList().eval());
    }

    @Test
    public void shouldEvaluateSequenceLazy_whenConditionSucceeds() throws InterruptedException {
        // Arrange
        final GlcStrategy<Object, Integer, Integer, Integer> strategy = GlcStrategy.getInstance();
        final TestListStrategy<Integer, Integer> sc = new TestListStrategy<>(it -> Arrays.asList(it + 0, it + 1, it + 2));
        final TestListStrategy<Integer, Integer> st = new TestListStrategy<>(it -> Arrays.asList(it + 5));
        final TestListStrategy<Integer, Integer> se = new TestListStrategy<>(it -> Arrays.asList(it * 10));

        // Act/Assert
        final Seq<Integer> result = strategy.eval(new Object(), sc, st, se, 42);
        assertEquals(1, sc.evalCalls.get());        // called once to get the lazy sequence
        assertEquals(0, st.evalCalls.get());        // not called until the sequence is evaluated
        assertEquals(0, se.evalCalls.get());        // not called until the sequence is evaluated

        final InterruptibleIterator<Integer> iterator = result.iterator();
        assertEquals(1, sc.iteratorCalls.get());    // called once to get the lazy sequence's iterator
        assertEquals(0, st.iteratorCalls.get());    // not called until the sequence is evaluated
        assertEquals(0, se.iteratorCalls.get());    // not called until the sequence is evaluated

        iterator.next();
        assertEquals(1, sc.nextCalls.get());        // called to get the first element
        assertEquals(1, st.nextCalls.get());        // called to get the first element
        assertEquals(0, se.nextCalls.get());        // not called

        iterator.next();
        assertEquals(2, sc.nextCalls.get());        // called to get the second element
        assertEquals(3, st.nextCalls.get());        // called to get the second element (which doesn't exist), then called to get the first element
        assertEquals(0, se.nextCalls.get());        // not called

        iterator.next();
        assertEquals(3, sc.nextCalls.get());        // called to get the third element
        assertEquals(5, st.nextCalls.get());        // called to get the second element (which doesn't exist), then called to get the first element
        assertEquals(0, se.nextCalls.get());        // not called

        iterator.hasNext();
        assertEquals(4, sc.nextCalls.get());        // called to determine there is no more
        assertEquals(6, st.nextCalls.get());        // called to determine there is no more
        assertEquals(0, se.nextCalls.get());        // not called

        // Final tally
        assertEquals(1, sc.evalCalls.get());
        assertEquals(3, st.evalCalls.get());
        assertEquals(0, se.evalCalls.get());
        assertEquals(1, sc.iteratorCalls.get());
        assertEquals(3, st.iteratorCalls.get());
        assertEquals(0, se.iteratorCalls.get());
        assertEquals(4, sc.nextCalls.get());
        assertEquals(6, st.nextCalls.get());
        assertEquals(0, se.nextCalls.get());
    }
}
