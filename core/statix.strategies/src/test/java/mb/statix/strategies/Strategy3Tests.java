package mb.statix.strategies;

import mb.statix.sequences.Computation;
import mb.statix.sequences.Seq;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link Strategy3} interface.
 */
@SuppressWarnings("CodeBlock2Expr")
public final class Strategy3Tests {
    @Test
    public void getArity_shouldReturn3() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final int arity = strategy.getArity();

        // Assert
        assertEquals(3, arity);
    }

    @Test
    public void apply1_getName_shouldReturnNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String name = appliedStrategy.getName();

        // Assert
        assertEquals("my-test-strategy", name);
    }

    @Test
    public void apply1_getParamName_shouldCallGetParamNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act/Assert
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String param0 = appliedStrategy.getParamName(0);
        final String param1 = appliedStrategy.getParamName(1);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            appliedStrategy.getParamName(2);
        });

        // Assert
        assertEquals("part2", param0);
        assertEquals("part3", param1);
    }

    @Test
    public void apply1_writeArg_shouldCallWriteArgOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        appliedStrategy.writeArg(sb, 0, "xyz");

        // Assert
        assertEquals("1: \"xyz\"", sb.toString());
    }

    @Test
    public void apply1_isAnonymous_shouldReturnIsAnonymousOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final boolean anonymous = appliedStrategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void apply1_isAtom_shouldReturnTrue() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final boolean atom = appliedStrategy.isAtom();

        // Assert
        assertTrue(atom);
    }

    @Test
    public void apply1_getPrecedence_shouldCallGetPrecedenceOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final int precedence = appliedStrategy.getPrecedence();

        // Assert
        assertEquals(strategy.getPrecedence(), precedence);
    }

    @Test
    public void apply1_writeTo_shouldWriteStrategyNameAndArguments() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String str = appliedStrategy.writeTo(new StringBuilder()).toString();

        // Assert
        assertEquals("my-test-strategy(0: \"Hello, \", ..)", str);
    }

    @Test
    public void apply1_eval_shouldImplicitlyApplyArguments() throws InterruptedException {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy2<Object, String, String, String, String> appliedStrategy = strategy.apply("Hello, ");
        final String result = appliedStrategy.eval(new Object(), "cruel ", "corona ","World").single().eval();

        // Assert
        assertEquals("Hello, cruel corona World", result);
    }



    @Test
    public void apply2_getName_shouldReturnNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String name = appliedStrategy.getName();

        // Assert
        assertEquals("my-test-strategy", name);
    }

    @Test
    public void apply2_getParamName_shouldCallGetParamNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act/Assert
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String param0 = appliedStrategy.getParamName(0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            appliedStrategy.getParamName(1);
        });

        // Assert
        assertEquals("part3", param0);
    }

    @Test
    public void apply2_writeArg_shouldCallWriteArgOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        appliedStrategy.writeArg(sb, 0, "xyz");

        // Assert
        assertEquals("2: \"xyz\"", sb.toString());
    }

    @Test
    public void apply2_isAnonymous_shouldReturnIsAnonymousOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final boolean anonymous = appliedStrategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void apply2_isAtom_shouldReturnTrue() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final boolean atom = appliedStrategy.isAtom();

        // Assert
        assertTrue(atom);
    }

    @Test
    public void apply2_getPrecedence_shouldCallGetPrecedenceOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final int precedence = appliedStrategy.getPrecedence();

        // Assert
        assertEquals(strategy.getPrecedence(), precedence);
    }

    @Test
    public void apply2_writeTo_shouldWriteStrategyNameAndArguments() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String str = appliedStrategy.writeTo(new StringBuilder()).toString();

        // Assert
        assertEquals("my-test-strategy(0: \"Hello, \", 1: \"beautiful \", ..)", str);
    }

    @Test
    public void apply2_eval_shouldImplicitlyApplyArguments() throws InterruptedException {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy1<Object, String, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ");
        final String result = appliedStrategy.eval(new Object(), "corona " , "World").single().eval();

        // Assert
        assertEquals("Hello, beautiful corona World", result);
    }



    @Test
    public void apply3_getName_shouldReturnNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final String name = appliedStrategy.getName();

        // Assert
        assertEquals("my-test-strategy", name);
    }

    @Test
    public void apply3_getParamName_shouldCallGetParamNameOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act/Assert
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            appliedStrategy.getParamName(0);
        });
    }

    @Test
    public void apply3_writeArg_shouldCallWriteArgOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();
        final StringBuilder sb = new StringBuilder();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        appliedStrategy.writeArg(sb, 0, "xyz");

        // Assert
        assertEquals("3: \"xyz\"", sb.toString());
    }

    @Test
    public void apply3_isAnonymous_shouldReturnIsAnonymousOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final boolean anonymous = appliedStrategy.isAnonymous();

        // Assert
        assertFalse(anonymous);
    }

    @Test
    public void apply3_isAtom_shouldReturnTrue() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final boolean atom = appliedStrategy.isAtom();

        // Assert
        assertTrue(atom);
    }

    @Test
    public void apply3_getPrecedence_shouldCallGetPrecedenceOfOriginalStrategy() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final int precedence = appliedStrategy.getPrecedence();

        // Assert
        assertEquals(strategy.getPrecedence(), precedence);
    }

    @Test
    public void apply3_writeTo_shouldWriteStrategyNameAndArguments() {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final String str = appliedStrategy.writeTo(new StringBuilder()).toString();

        // Assert
        assertEquals("my-test-strategy(0: \"Hello, \", 1: \"beautiful \", 2: \"new \")", str);
    }

    @Test
    public void apply3_eval_shouldImplicitlyApplyArguments() throws InterruptedException {
        // Arrange
        final Strategy3<Object, String, String, String, String, String> strategy = new MyTestStrategy();

        // Act
        final Strategy<Object, String, String> appliedStrategy = strategy.apply("Hello, ", "beautiful ", "new ");
        final String result = appliedStrategy.eval(new Object(), "World").single().eval();

        // Assert
        assertEquals("Hello, beautiful new World", result);
    }

    private static class MyTestStrategy extends NamedStrategy3<Object, String, String, String, String, String> {
        @Override
        public Seq<String> doEval(Object ctx, String part1, String part2, String part3, String input) {
            return Computation.fromOnly(() -> part1 + part2 + part3 + input);
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
                case 2: return "part3";
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
