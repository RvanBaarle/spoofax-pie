package mb.statix.strategies.runtime;

import mb.statix.lazy.LazySeq;
import mb.statix.sequences.Computation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the {@link IdStrategy} class.
 */
public final class FailStrategyTests {

    @Test
    public void shouldAlwaysFail() throws InterruptedException {
        // Arrange
        final FailStrategy<Object, String, Integer> strategy = FailStrategy.getInstance();

        // Act
        final LazySeq<Integer> result = strategy.eval(new Object(), "My input");

        // Assert
        assertFalse(result.next());
    }

}
