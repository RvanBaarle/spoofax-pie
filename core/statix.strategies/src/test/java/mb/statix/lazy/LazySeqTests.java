package mb.statix.lazy;

import mb.statix.sequences.InterruptibleSupplier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link LazySeq} class.
 */
@SuppressWarnings("Convert2MethodRef")
public final class LazySeqTests {

    @Test
    public void of_shouldReturnEmptySequence_whenGivenNoArguments() throws InterruptedException {
        // Act
        final LazySeq<Integer> seq = LazySeq.of();

        // Assert
        assertFalse(seq.next());
        assertThrows(Exception.class, () -> seq.getCurrent(), "Undefined behavior");
    }

    @Test
    public void of_shouldReturnSequence_whenGivenArguments() throws InterruptedException {
        // Assert
        final int a = 10;
        final int b = 20;
        final int c = 30;

        // Act
        final LazySeq<Integer> seq = LazySeq.of(a, b, c);

        // Assert
        assertThrows(Exception.class, () -> seq.getCurrent(), "Undefined behavior");
        assertTrue(seq.next());
        assertEquals(a, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(b, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(c, seq.getCurrent());
        assertFalse(seq.next());
        assertThrows(Exception.class, () -> seq.getCurrent(), "Undefined behavior");
    }

    @Test
    public void of_shouldReturnEmptySequence_whenGivenEmptyArray() throws InterruptedException {
        // Act
        final LazySeq<Integer> seq = LazySeq.of(new Integer[0]);

        // Assert
        assertFalse(seq.next());
    }

    @Test
    public void from_shouldWrapSupplier() throws InterruptedException {
        // Arrange
        final AtomicInteger counter = new AtomicInteger();
        final InterruptibleSupplier<Integer> supplier = counter::getAndIncrement;

        // Act
        final LazySeq<Integer> seq = LazySeq.from(supplier);

        // Assert
        assertTrue(seq.next());
        assertEquals(0, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(1, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(2, seq.getCurrent());
        // ... Infinite sequence
    }

    @Test
    public void from_shouldFinishAndThrow_whenSupplierThrows() throws InterruptedException {
        // Arrange
        final AtomicInteger counter = new AtomicInteger();
        final InterruptibleSupplier<Integer> supplier = () -> {
            if (counter.get() > 2) throw new IllegalStateException("Exception thrown");
            return counter.getAndIncrement();
        };

        // Act
        final LazySeq<Integer> seq = LazySeq.from(supplier);

        // Assert
        assertTrue(seq.next());
        assertEquals(0, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(1, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(2, seq.getCurrent());
        assertThrows(IllegalStateException.class, () -> seq.next());
    }

    @Test
    public void from_shouldFinish_whenSupplierThrowsNoSuchElementException() throws InterruptedException {
        // Arrange
        final AtomicInteger counter = new AtomicInteger();
        final InterruptibleSupplier<Integer> supplier = () -> {
            if (counter.get() > 2) throw new NoSuchElementException("Done!");
            return counter.getAndIncrement();
        };

        // Act
        final LazySeq<Integer> seq = LazySeq.from(supplier);

        // Assert
        assertTrue(seq.next());
        assertEquals(0, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(1, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(2, seq.getCurrent());
        assertFalse(seq.next());
    }

    @Test
    public void from_shouldCloseSupplier_whenSupplierIsAutoCloseable() throws Exception {
        // Arrange
        final AtomicBoolean closed = new AtomicBoolean();
        final AtomicInteger counter = new AtomicInteger();
        final InterruptibleSupplier<Integer> supplier = new CloseableSupplier<Integer>() {
            @Override
            public Integer get() throws InterruptedException {
                return counter.getAndIncrement();
            }

            @Override
            public void close() throws Exception {
                final boolean nowClosed = closed.compareAndSet(false, true);
                if (!nowClosed) throw new IllegalStateException("Already closed");
            }
        };

        // Act
        final LazySeq<Integer> seq = LazySeq.from(supplier);

        // Assert
        assertFalse(closed.get());
        seq.close();
        assertTrue(closed.get());
    }

    @Test
    public void asSeq_shouldReturnIteratorValues_whenWrappingIterator() throws InterruptedException {
        // Arrange
        final Iterator<Integer> iterator = Arrays.asList(0, 1, 2).listIterator();

        // Act
        final LazySeq<Integer> seq = LazySeq.asSeq(iterator);

        // Assert
        assertTrue(seq.next());
        assertEquals(0, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(1, seq.getCurrent());
        assertTrue(seq.next());
        assertEquals(2, seq.getCurrent());
        assertFalse(seq.next());
    }

    @Test
    public void asSeq_shouldReturnOriginalSequence_whenWrappingIteratorWrappingSequence() throws InterruptedException {
        // Arrange
        final LazySeq<Integer> seq = LazySeq.of(0, 1, 2);
        final Iterator<Integer> iterator = LazySeq.asIterator(seq);

        // Act
        final LazySeq<Integer> newSeq = LazySeq.asSeq(iterator);

        // Assert
        assertSame(seq, newSeq);
    }

    @Test
    public void asIterator_shouldReturnSequenceValues_whenWrappingSequence() throws InterruptedException {
        // Arrange
        final LazySeq<Integer> seq = LazySeq.of(0, 1, 2);

        // Act
        final Iterator<Integer> iterator = LazySeq.asIterator(seq);

        // Assert
        assertTrue(iterator.hasNext());
        assertEquals(0, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }


    @Test
    public void asIterator_shouldReturnOriginalIterator_whenWrappingSequenceWrappingIterator() throws InterruptedException {
        // Arrange
        final Iterator<Integer> iterator = Arrays.asList(0, 1, 2).listIterator();
        final LazySeq<Integer> seq = LazySeq.asSeq(iterator);

        // Act
        final Iterator<Integer> newIterator = LazySeq.asIterator(seq);

        // Assert
        assertSame(iterator, newIterator);
    }

    @Test
    public void asIterator_shouldCloseSequence() throws Exception {
        // Arrange
        final AtomicBoolean closed = new AtomicBoolean();
        final LazySeq<Integer> seq = new LazySeqBase<Integer>() {
            @Override
            protected void computeNext() throws InterruptedException {
                yieldBreak();
            }

            @Override
            public void close() throws Exception {
                final boolean nowClosed = closed.compareAndSet(false, true);
                if (!nowClosed) throw new IllegalStateException("Already closed");
            }
        };

        // Act
        final Iterator<Integer> iterator = LazySeq.asIterator(seq);

        // Assert
        assertFalse(closed.get());
        ((AutoCloseable)iterator).close();
        assertTrue(closed.get());
    }

    private interface CloseableSupplier<T> extends InterruptibleSupplier<T>, AutoCloseable { }

}
