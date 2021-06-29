package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.Seq;
import mb.statix.strategies.TestListStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link AndStrategy} class.
 */
@SuppressWarnings({"PointlessArithmeticExpression", "ArraysAsListWithZeroOrOneArgument"}) public final class AndStrategyTests {

    @Test
    public void shouldEvaluateFirstSequenceThenSecondSequence() throws InterruptedException {
        // Arrange
        final AndStrategy<Object, Integer, Integer> strategy = AndStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s1 = new TestListStrategy<>(it -> Arrays.asList(it + 1, it + 2, it + 3));
        final TestListStrategy<Integer, Integer> s2 = new TestListStrategy<>(it -> Arrays.asList(it * 1, it * 2, it * 3));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s1, s2, 42);

        // Assert
        assertEquals(Arrays.asList(43, 44, 45, 42, 84, 126), result.toList().eval());
    }

    @Test
    public void shouldEvaluateToEmptySequence_whenSecondSequenceIsEmpty() throws InterruptedException {
        // Arrange
        final AndStrategy<Object, Integer, Integer> strategy = AndStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s1 = new TestListStrategy<>(it -> Arrays.asList(it + 1, it + 2, it + 3));
        final TestListStrategy<Integer, Integer> s2 = new TestListStrategy<>(it -> Arrays.asList());

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s1, s2, 42);

        // Assert
        assertEquals(Arrays.asList(), result.toList().eval());
    }

    @Test
    public void shouldEvaluateToEmptySequence_whenFirstSequenceIsEmpty() throws InterruptedException {
        // Arrange
        final AndStrategy<Object, Integer, Integer> strategy = AndStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s1 = new TestListStrategy<>(it -> Arrays.asList());
        final TestListStrategy<Integer, Integer> s2 = new TestListStrategy<>(it -> Arrays.asList(it * 1, it * 2, it * 3));

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s1, s2, 42);

        // Assert
        assertEquals(Arrays.asList(), result.toList().eval());
    }

    @Test
    public void shouldEvaluateToEmptySequence_whenBothSequencesAreEmpty() throws InterruptedException {
        // Arrange
        final AndStrategy<Object, Integer, Integer> strategy = AndStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s1 = new TestListStrategy<>(it -> Arrays.asList());
        final TestListStrategy<Integer, Integer> s2 = new TestListStrategy<>(it -> Arrays.asList());

        // Act
        final Seq<Integer> result = strategy.eval(new Object(), s1, s2, 42);

        // Assert
        assertEquals(Arrays.asList(), result.toList().eval());
    }

    @Test
    public void shouldEvaluateSequenceLazy() throws InterruptedException {
        // Arrange
        final AndStrategy<Object, Integer, Integer> strategy = AndStrategy.getInstance();
        final TestListStrategy<Integer, Integer> s1 = new TestListStrategy<>(it -> Arrays.asList(it + 1, it + 2, it + 3));
        final TestListStrategy<Integer, Integer> s2 = new TestListStrategy<>(it -> Arrays.asList(it * 1, it * 2, it * 3));

        // Act/Assert
        final Seq<Integer> result = strategy.eval(new Object(), s1, s2, 42);
        assertEquals(1, s1.evalCalls.get());        // called once to get the lazy sequence
        assertEquals(1, s2.evalCalls.get());        // called once to get the lazy sequence

        final InterruptibleIterator<Integer> iterator = result.iterator();
        assertEquals(1, s1.iteratorCalls.get());    // called once to get the first lazy sequence's iterator
        assertEquals(1, s2.iteratorCalls.get());    // called once to get the second lazy sequence's iterator

        iterator.next();
        assertEquals(1, s1.nextCalls.get());        // called to get the first element
        assertEquals(1, s2.nextCalls.get());        // called to determine whether there is a first element

        iterator.next();
        assertEquals(2, s1.nextCalls.get());        // called to get the second element
        assertEquals(1, s2.nextCalls.get());        // not called yet

        iterator.next();
        assertEquals(3, s1.nextCalls.get());        // called to get the third element
        assertEquals(1, s2.nextCalls.get());        // not called yet

        iterator.next();
        assertEquals(4, s1.nextCalls.get());        // called to find the sequence empty
        assertEquals(1, s2.nextCalls.get());        // not called again, first element was already computed

        iterator.next();
        assertEquals(2, s2.nextCalls.get());        // called to get the second element

        iterator.next();
        assertEquals(3, s2.nextCalls.get());        // called to get the third element

        iterator.hasNext();
        assertEquals(4, s2.nextCalls.get());        // called to find the sequence empty

        // Final tally
        assertEquals(1, s1.evalCalls.get());
        assertEquals(1, s2.evalCalls.get());
        assertEquals(1, s1.iteratorCalls.get());
        assertEquals(1, s2.iteratorCalls.get());
        assertEquals(4, s1.nextCalls.get());
        assertEquals(4, s2.nextCalls.get());
    }
}
