package mb.statix.strategies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@link NamedStrategy2} class.
 */
public final class NamedStrategy2Tests {

    @Test
    public void eval_shouldCallDoEval() {
        // Arrange
        final MyTestStrategy2 strategy = new MyTestStrategy2();

        // Act
        strategy.eval(new Object(), "Hello, ", "cruel ", "world!");

        // Assert
        assertEquals(1, strategy.doEvalCalls.get());
    }

    @Test
    public void isAnonymous_shouldReturnFalse() {
        // Arrange
        final MyTestStrategy2 strategy = new MyTestStrategy2();

        // Act
        final boolean anonymous = strategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void writeTo_shouldWriteNameToStringBuilder() {
        // Arrange
        final MyTestStrategy2 strategy = new MyTestStrategy2();
        final StringBuilder sb = new StringBuilder();

        // Act
        strategy.writeTo(sb);

        // Assert
        assertEquals("my-test-strategy-2", sb.toString());
    }

    @Test
    public void toString_shouldReturnName() {
        // Arrange
        final MyTestStrategy2 strategy = new MyTestStrategy2();

        // Act
        final String result = strategy.toString();

        // Assert
        assertEquals("my-test-strategy-2", result);
    }

}
