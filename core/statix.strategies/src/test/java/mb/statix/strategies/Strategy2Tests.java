package mb.statix.strategies;

import mb.statix.sequences.Computation;
import mb.statix.sequences.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link Strategy2} interface.
 */
@SuppressWarnings("CodeBlock2Expr")
public final class Strategy2Tests {
    @Test
    public void getArity_shouldReturn2() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final int arity = strategy.getArity();

        // Assert
        assertEquals(2, arity);
    }

    @Test
    public void apply1_getName_shouldReturnNameOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String name = appliedStrategy.getName();

        // Assert
        assertEquals("my-test-strategy", name);
    }

    @Test
    public void apply1_getParamName_shouldCallGetParamNameOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act/Assert
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String param0 = appliedStrategy.getParamName(0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            appliedStrategy.getParamName(1);
        });

        // Assert
        assertEquals("part2", param0);
    }

    @Test
    public void apply1_writeArg_shouldCallWriteArgOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        appliedStrategy.writeArg(sb, 0, "xyz");

        // Assert
        assertEquals("1: \"xyz\"", sb.toString());
    }

    @Test
    public void apply1_isAnonymous_shouldReturnIsAnonymousOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final boolean anonymous = appliedStrategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void apply1_isAtom_shouldReturnTrue() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final boolean atom = appliedStrategy.isAtom();

        // Assert
        assertTrue(atom);
    }

    @Test
    public void apply1_getPrecedence_shouldCallGetPrecedenceOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final int precedence = appliedStrategy.getPrecedence();

        // Assert
        assertEquals(strategy.getPrecedence(), precedence);
    }

    @Test
    public void apply1_writeTo_shouldWriteStrategyNameAndArguments() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String str = appliedStrategy.writeTo(new StringBuilder()).toString();

        // Assert
        assertEquals("my-test-strategy(0: \"Hello, \", ..)", str);
    }

    @Test
    public void apply1_eval_shouldImplicitlyApplyArguments() throws InterruptedException {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String result = appliedStrategy.eval(new Object(), "cruel ", "World").single().eval();

        // Assert
        assertEquals("Hello, cruel World", result);
    }



    @Test
    public void apply2_getName_shouldReturnNameOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String name = appliedStrategy.getName();

        // Assert
        assertEquals("my-test-strategy", name);
    }

    @Test
    public void apply2_getParamName_shouldCallGetParamNameOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act/Assert
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            appliedStrategy.getParamName(0);
        });
    }

    @Test
    public void apply2_writeArg_shouldCallWriteArgOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        appliedStrategy.writeArg(sb, 0, "xyz");

        // Assert
        assertEquals("2: \"xyz\"", sb.toString());
    }

    @Test
    public void apply2_isAnonymous_shouldReturnIsAnonymousOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final boolean anonymous = appliedStrategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void apply2_isAtom_shouldReturnTrue() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final boolean atom = appliedStrategy.isAtom();

        // Assert
        assertTrue(atom);
    }

    @Test
    public void apply2_getPrecedence_shouldCallGetPrecedenceOfOriginalStrategy() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final int precedence = appliedStrategy.getPrecedence();

        // Assert
        assertEquals(strategy.getPrecedence(), precedence);
    }

    @Test
    public void apply2_writeTo_shouldWriteStrategyNameAndArguments() {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String str = appliedStrategy.writeTo(new StringBuilder()).toString();

        // Assert
        assertEquals("my-test-strategy(0: \"Hello, \", 1: \"beautiful \")", str);
    }

    @Test
    public void apply2_eval_shouldImplicitlyApplyArguments() throws InterruptedException {
        // Arrange
        final Strategy2<Object, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String result = appliedStrategy.eval(new Object(), "World").single().eval();

        // Assert
        assertEquals("Hello, beautiful World", result);
    }

    private static class MyTestStrategy extends NamedStrategy2<Object, String, String, String, String> {
        @Override
        public Seq<String> doEval(Object ctx, String part1, String part2, String input) {
            return Computation.fromOnly(() -> part1 + part2 + input);
        }

        @Override
        public String getName() {
            return "my-test-strategy";
        }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return "part1";
                case 1: return "part2";
                default: throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
            }
        }

        @Override
        public int getPrecedence() {
            return 42;
        }

        @Override
        public void writeArg(StringBuilder sb, int index, Object arg) {
            sb.append(index).append(": ");    // Prefix added for testing
            super.writeArg(sb, index, arg);
        }
    }
}
