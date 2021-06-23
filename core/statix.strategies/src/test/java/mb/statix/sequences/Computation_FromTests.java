package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Computation#from}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_FromTests {

    @Test
    public void returnsFailingComputation_whenSupplierReturnsNull() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.from(() -> null);

        // Act/Assert
        assertNull(sut.tryEval());
    }

    @Test
    public void returnsValue_whenSupplierReturnsValue() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.from(() -> 42);

        // Act
        @Nullable final Integer result = sut.tryEval();

        // Assert
        assertEquals(42, result);
    }

}
