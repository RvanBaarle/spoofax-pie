package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Computation#empty}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_EmptyTests {

    @Test
    public void returnsFailingComputation() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.empty();

        // Act/Assert
        assertNull(sut.tryEval());
    }

}
