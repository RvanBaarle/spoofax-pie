package mb.statix.strategies.runtime;

import mb.statix.sequences.Computation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link IdStrategy} class.
 */
public final class IdStrategyTests {

    @Test
    public void shouldReturnComputationOfInput() throws InterruptedException {
        // Arrange
        final IdStrategy<Object, String> strategy = IdStrategy.getInstance();
        final String input = "My input";

        // Act
        final Computation<String> result = strategy.eval(new Object(), input);

        // Assert
        assertEquals(input, result.eval());
    }

}
