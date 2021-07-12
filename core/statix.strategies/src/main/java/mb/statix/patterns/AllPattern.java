package mb.statix.patterns;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A pattern that matches everything.
 *
 * @param <CTX> the type of context (invariant)
 */
public final class AllPattern<CTX> implements Pattern<CTX, Object> {

    @SuppressWarnings("rawtypes")
    private static final AllPattern instance = new AllPattern();
    @SuppressWarnings("unchecked")
    public static <CTX> AllPattern<CTX> getInstance() { return (AllPattern<CTX>)instance; }

    private AllPattern() { /* Prevent instantiation. Use getInstance(). */ }

    @Override
    public boolean match(CTX ctx, @Nullable Object input) {
        return true;
    }

}
