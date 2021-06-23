package mb.statix.sequences;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Seq#distinctBy}.
 */
public final class Seq_DistinctByTests {

    @Test
    public void shouldReturnDistinctElementsByValue_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<SomeTuple> input = Seq.from(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("b", 1),
            new SomeTuple("c", 4),
            new SomeTuple("d", 2),
            new SomeTuple("a", 5),
            new SomeTuple("b", 5)
        ));

        // Act
        Seq<SomeTuple> distinct = input.distinctBy(t -> t.value);

        // Assert
        assertEquals(new HashSet<>(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("c", 4),
            new SomeTuple("a", 5)
        )), new HashSet<>(distinct.toList().eval()));
    }

    @Test
    public void shouldReturnDistinctElementsByName_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<SomeTuple> input = Seq.from(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("b", 1),
            new SomeTuple("c", 4),
            new SomeTuple("d", 2),
            new SomeTuple("a", 5),
            new SomeTuple("b", 5)
        ));

        // Act
        Seq<SomeTuple> distinct = input.distinctBy(t -> t.name);

        // Assert
        assertEquals(new HashSet<>(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2)
        )), new HashSet<>(distinct.toList().eval()));
    }

    @Test
    public void shouldKeepOrderOfDistinctElementsByValue_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<SomeTuple> input = Seq.from(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("b", 1),
            new SomeTuple("c", 4),
            new SomeTuple("d", 2),
            new SomeTuple("a", 5),
            new SomeTuple("b", 5)
        ));

        // Act
        Seq<SomeTuple> distinct = input.distinctBy(t -> t.value);

        // Assert
        assertEquals(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("c", 4),
            new SomeTuple("a", 5)
        ), distinct.toList().eval());
    }

    @Test
    public void shouldKeepOrderOfDistinctElementsByName_whenEvaluated() throws InterruptedException {
        // Arrange
        Seq<SomeTuple> input = Seq.from(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2),
            new SomeTuple("a", 3),
            new SomeTuple("b", 1),
            new SomeTuple("c", 4),
            new SomeTuple("d", 2),
            new SomeTuple("a", 5),
            new SomeTuple("b", 5)
        ));

        // Act
        Seq<SomeTuple> distinct = input.distinctBy(t -> t.name);

        // Assert
        assertEquals(Arrays.asList(
            new SomeTuple("a", 0),
            new SomeTuple("b", 1),
            new SomeTuple("c", 1),
            new SomeTuple("d", 2)
        ), distinct.toList().eval());
    }

    private final static class SomeTuple {
        public String name;
        public int value;
        public SomeTuple(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            SomeTuple someTuple = (SomeTuple)o;
            return value == someTuple.value && name.equals(someTuple.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override public String toString() {
            return "SomeTuple(" + "name=\"" + name + '\"' + ", value=" + value + ')';
        }
    }
}
