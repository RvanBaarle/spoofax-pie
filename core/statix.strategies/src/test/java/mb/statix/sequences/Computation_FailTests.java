package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Computation#fail}.
 */
@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
public final class Computation_FailTests {

    @Test
    public void returnsFailingComputation() throws InterruptedException {
        // Arrange
        final Computation<Integer> sut = Computation.fail();

        // Act/Assert
        assertNull(sut.tryEval());
    }

}
