package mb.data;

import java.util.Objects;

/**
 * A tuple with two values.
 *
 * This class is intended to be replaced by a value class in future Java versions.
 * Therefore, this class is not extensible, and its members are read-only.
 *
 * @param <T1> the type of the first component (covariant)
 * @param <T2> the type of the second component (covariant)
 */
public final class Tuple2<T1, T2> {

    private final T1 component1;
    private final T2 component2;

    /**
     * Gets the first component in this tuple.
     * @return the first component
     */
    public final T1 getComponent1() { return this.component1; }

    /**
     * Gets the second component in this tuple.
     * @return the second component
     */
    public final T2 getComponent2() { return this.component2; }

    /**
     * Creates a new instance of the {@link Tuple2} class.
     *
     * @param component1 the first component of the tuple
     * @param component2 the second component of the tuple
     * @param <T1> the type of the first component (covariant)
     * @param <T2> the type of the second component (covariant)
     * @return the created tuple
     */
    public static <T1, T2> Tuple2<T1, T2> of(T1 component1, T2 component2) {
        return new Tuple2<>(component1, component2);
    }

    /**
     * Initializes a new instance of the {@link Tuple2} class.
     *
     * @param component1 the first component of the tuple
     * @param component2 the second component of the tuple
     */
    private Tuple2(T1 component1, T2 component2) {
        this.component1 = component1;
        this.component2 = component2;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        // This `instanceof` check is safe as the class is `final`.
        if(!(o instanceof Tuple2)) return false;
        Tuple2<?, ?> that = (Tuple2<?, ?>)o;
        return Objects.equals(this.component1, that.component1)
            && Objects.equals(this.component2, that.component2);
    }

    @Override public int hashCode() {
        final T1 t1 = this.component1;
        final T2 t2 = this.component2;
        int hash = 7;
        hash = 31 * hash + (t1 != null ? t1.hashCode() : 0);
        hash = 31 * hash + (t2 != null ? t2.hashCode() : 0);
        return hash;
    }

    @Override public String toString() {
        return "(" + this.component1 + ", " + component2 + ")";
    }
}
