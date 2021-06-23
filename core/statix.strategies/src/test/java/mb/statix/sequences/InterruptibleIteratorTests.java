package mb.statix.sequences;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link InterruptibleIterator} interface.
 */
@SuppressWarnings({"Convert2MethodRef", "RedundantThrows", "Convert2Diamond"}) public final class InterruptibleIteratorTests {

    @Test
    public void forEachRemaining_shouldApplyActionToEachRemainingElement() throws InterruptedException {
        // Arrange
        final InterruptibleIterator<String> sut = new InterruptibleIterator<String>() {
            private int i = 0;
            private final String[] elements = new String[] { "a", "b", "c" };

            @Override
            public boolean hasNext() throws InterruptedException {
                return i < elements.length;
            }

            @Override
            public String next() throws InterruptedException {
                final String element = elements[i];
                i += 1;
                return element;
            }
        };
        final ArrayList<String> results = new ArrayList<>();

        // Act/Assert
        assertTrue(sut.hasNext());
        assertEquals("a", sut.next());

        sut.forEachRemaining(results::add);

        assertEquals(Arrays.asList("b", "c"), results);
    }

    @Test
    public void wrap_shouldWrapANormalIterator() throws InterruptedException {
        // Arrange
        final Iterator<String> iterator = Arrays.asList("a", "b", "c").iterator();
        final InterruptibleIterator<String> sut = InterruptibleIterator.wrap(iterator);

        // Assert
        assertTrue(sut.hasNext());
        assertEquals("a", sut.next());
        assertTrue(sut.hasNext());
        assertEquals("b", sut.next());
        assertTrue(sut.hasNext());
        assertEquals("c", sut.next());
        assertFalse(sut.hasNext());
    }

    @Test
    public void wrap_shouldReturnWrappedInterruptibleIteratorIfAny() {
        // Arrange
        final InterruptibleIterator<String> iterator = new InterruptibleIterator<String>() {
            private int i = 0;
            private final String[] elements = new String[] { "a", "b", "c" };

            @Override
            public boolean hasNext() throws InterruptedException {
                return i < elements.length;
            }

            @Override
            public String next() throws InterruptedException {
                final String element = elements[i];
                i += 1;
                return element;
            }
        };
        final Iterator<String> unwrapped = InterruptibleIterator.unwrap(iterator);

        // Act
        final InterruptibleIterator<String> wrapped = InterruptibleIterator.wrap(unwrapped);

        // Assert
        assertSame(iterator, wrapped);
    }

    @Test
    public void unwrap_shouldProduceANormalIterator() {
        // Arrange
        final InterruptibleIterator<String> sut = new InterruptibleIterator<String>() {
            private int i = 0;
            private final String[] elements = new String[] { "a", "b", "c" };

            @Override
            public boolean hasNext() throws InterruptedException {
                return i < elements.length;
            }

            @Override
            public String next() throws InterruptedException {
                final String element = elements[i];
                i += 1;
                return element;
            }
        };
        final Iterator<String> iterator = InterruptibleIterator.unwrap(sut);

        // Assert
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("c", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void unwrap_shouldThrowRuntimeExceptionWhenIteratorNextThrowsInterruptedException() {
        // Arrange
        final InterruptibleIterator<String> sut = new InterruptibleIterator<String>() {
            private int i = 0;
            private final @Nullable String[] elements = new @Nullable String[] { "a", "b", null};

            @Override
            public boolean hasNext() throws InterruptedException {
                return i < elements.length;
            }

            @Override
            public String next() throws InterruptedException {
                @Nullable final String element = elements[i];
                if (element == null) {
                    throw new InterruptedException("Interrupted!");
                }
                i += 1;
                return element;
            }
        };
        final Iterator<String> iterator = InterruptibleIterator.unwrap(sut);

        // Assert
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next());
        assertTrue(iterator.hasNext());
        final RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            iterator.next();
        });
        assertTrue(ex.getCause() instanceof InterruptedException);
    }

    @Test
    public void unwrap_shouldThrowRuntimeExceptionWhenIteratorHasNextThrowsInterruptedException() {
        // Arrange
        final InterruptibleIterator<String> sut = new InterruptibleIterator<String>() {
            private int i = 0;
            private final @Nullable String[] elements = new @Nullable String[] { "a", "b", null};

            @Override
            public boolean hasNext() throws InterruptedException {
                boolean hasNext = i < elements.length;
                if (elements[i] == null) {
                    throw new InterruptedException("Interrupted!");
                }
                return hasNext;
            }

            @Override
            public String next() throws InterruptedException {
                @Nullable final String element = elements[i];
                i += 1;
                return element;
            }
        };
        final Iterator<String> iterator = InterruptibleIterator.unwrap(sut);

        // Assert
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        final RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            iterator.hasNext();
        });
        assertTrue(ex.getCause() instanceof InterruptedException);
    }

    @Test
    public void unwrap_shouldReturnWrappedIteratorIfAny() {
        // Arrange
        final Iterator<String> iterator = Arrays.asList("a", "b", "c").iterator();
        final InterruptibleIterator<String> sut = InterruptibleIterator.wrap(iterator);

        // Act
        final Iterator<String> unwrapped = InterruptibleIterator.unwrap(sut);

        // Assert
        assertSame(iterator, unwrapped);
    }

}
