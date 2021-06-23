package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Computation#of}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_OfTests {

    @Test
    public void returnsFailingComputation_whenGivenNoArguments() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.of();

        // Act/Assert
        assertNull(sut.tryEval());
    }

    @Test
    public void returnsValue_whenGivenOneArgument() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.of(42);

        // Act
        @Nullable final Integer result = sut.tryEval();

        // Assert
        assertEquals(42, result);
    }

}
