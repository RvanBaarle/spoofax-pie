package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link InterruptibleIteratorBase} interface.
 */
@SuppressWarnings({"Convert2MethodRef", "RedundantThrows", "Convert2Diamond"}) public final class InterruptibleIteratorBaseTests {

    @Test
    public void next_shouldReturnElements() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new ExampleIterator(new String[] { "a", "b", "c" });

        // Act/Assert
        assertEquals("a", sut.next());
        assertEquals("b", sut.next());
        assertEquals("c", sut.next());
    }

    @Test
    public void hasNext_shouldReturnWhetherThereAreMore() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new ExampleIterator(new String[] { "a", "b", "c" });

        // Act/Assert
        assertTrue(sut.hasNext());
        sut.next();
        assertTrue(sut.hasNext());
        sut.next();
        assertTrue(sut.hasNext());
        sut.next();
        assertFalse(sut.hasNext());
    }

    @Test
    public void next_shouldThrowNoSuchElementExceptionWhenThereAreNoMoreElements() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new ExampleIterator(new String[] { "a", "b", "c" });

        // Act/Assert
        sut.next();
        sut.next();
        sut.next();
        assertThrows(NoSuchElementException.class, () -> {
            sut.next();
        });
    }

    @Test
    public void setNext_shouldAssertItIsNeverCalledTwice() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new InterruptibleIteratorBase<String>() {
            @Override
            protected void computeNext() throws InterruptedException {
                yield("a");
                yield("b");
            }
        };

        // Act/Assert
        assertThrows(AssertionError.class, () -> {
            sut.next();
        });
    }

    @Test
    public void setNext_shouldAssertItIsNeverCalledAfterFinished() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new InterruptibleIteratorBase<String>() {
            @Override
            protected void computeNext() throws InterruptedException {
                yieldBreak();
                yield("b");
            }
        };

        // Act/Assert
        assertThrows(AssertionError.class, () -> {
            sut.next();
        });
    }

    @Test
    public void finished_shouldAssertItIsNeverCalledAfterSetNext() throws InterruptedException {
        // Arrange
        final InterruptibleIteratorBase<String> sut = new InterruptibleIteratorBase<String>() {
            @Override
            protected void computeNext() throws InterruptedException {
                yield("a");
                yieldBreak();
            }
        };

        // Act/Assert
        assertThrows(AssertionError.class, () -> {
            sut.next();
        });
    }

    private static final class ExampleIterator extends InterruptibleIteratorBase<String> {
        private int i = 0;
        private final String[] elements;

        public ExampleIterator(String[] elements) {
            this.elements = elements;
        }

        @Override
        protected void computeNext() throws InterruptedException {
            if (i < elements.length) {
                yield(elements[i]);
                i += 1;
            } else {
                yieldBreak();
            }
        }
    }
}
