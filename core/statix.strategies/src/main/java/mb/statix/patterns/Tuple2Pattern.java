package mb.statix.patterns;

import mb.statix.tuples.Tuple2;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A pattern for a tuple of two components.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T1> the type of the first component (contravariant)
 * @param <T2> the type of the second component (contravariant)
 */
public final class Tuple2Pattern<CTX, T1, T2> implements Pattern<CTX, Tuple2<T1, T2>> {

    private final Pattern<CTX, T1> p1;
    private final Pattern<CTX, T2> p2;

    /**
     * Initializes a new instance of the {@link Tuple2Pattern} class.
     *
     * @param p1 the pattern of the first component
     * @param p2 the pattern of the second component
     */
    public Tuple2Pattern(Pattern<CTX, T1> p1, Pattern<CTX, T2> p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public boolean match(CTX ctx, @Nullable Object input) {
        if (!(input instanceof Tuple2)) return false;
        @SuppressWarnings("unchecked")
        final Tuple2<@Nullable Object, @Nullable Object> tuple = (Tuple2<Object, Object>)input;
        return p1.match(ctx, tuple.getItem1())
            && p2.match(ctx, tuple.getItem2());
    }

}
