package mb.statix.strategies;

import mb.statix.lazy.LazySeq;
import mb.statix.sequences.Computation;
import mb.statix.sequences.Seq;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link Strategy} interface.
 */
public final class StrategyTests {
    @Test
    public void getArity_shouldReturn0() {
        // Arrange
        final Strategy<Object, String, Integer> strategy = (o, input) -> LazySeq.of(input.length());

        // Act
        final int arity = strategy.getArity();

        // Assert
        assertEquals(0, arity);
    }
}
