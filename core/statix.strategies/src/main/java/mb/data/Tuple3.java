package mb.data;

import java.util.Objects;

/**
 * A tuple with three values.
 *
 * This class is intended to be replaced by a value class in future Java versions.
 * Therefore, this class is not extensible, and its members are read-only.
 *
 * @param <T1> the type of the first component (covariant)
 * @param <T2> the type of the second component (covariant)
 * @param <T3> the type of the third component (covariant)
 */
public final class Tuple3<T1, T2, T3> {

    private final T1 component1;
    private final T2 component2;
    private final T3 component3;

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
     * Gets the third component in this tuple.
     * @return the third component
     */
    public final T3 getComponent3() { return this.component3; }

    /**
     * Creates a new instance of the {@link Tuple3} class.
     *
     * @param component1 the first component of the tuple
     * @param component2 the second component of the tuple
     * @param component3 the third component of the tuple
     * @param <T1> the type of the first component (covariant)
     * @param <T2> the type of the second component (covariant)
     * @param <T3> the type of the third component (covariant)
     * @return the created tuple
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 component1, T2 component2, T3 component3) {
        return new Tuple3<>(component1, component2, component3);
    }

    /**
     * Initializes a new instance of the {@link Tuple3} class.
     *
     * @param component1 the first component of the tuple
     * @param component2 the second component of the tuple
     * @param component3 the third component of the tuple
     */
    private Tuple3(T1 component1, T2 component2, T3 component3) {
        this.component1 = component1;
        this.component2 = component2;
        this.component3 = component3;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        // This `instanceof` check is safe as the class is `final`.
        if(!(o instanceof Tuple3)) return false;
        Tuple3<?, ?, ?> that = (Tuple3<?, ?, ?>)o;
        return Objects.equals(this.component1, that.component1)
            && Objects.equals(this.component2, that.component2)
            && Objects.equals(this.component3, that.component3);
    }

    @Override public int hashCode() {
        final T1 t1 = this.component1;
        final T2 t2 = this.component2;
        final T3 t3 = this.component3;
        int hash = 7;
        hash = 31 * hash + (t1 != null ? t1.hashCode() : 0);
        hash = 31 * hash + (t2 != null ? t2.hashCode() : 0);
        hash = 31 * hash + (t3 != null ? t3.hashCode() : 0);
        return hash;
    }

    @Override public String toString() {
        return "(" + this.component1 + ", " + component2 + ", " + component3 + ")";
    }
}
