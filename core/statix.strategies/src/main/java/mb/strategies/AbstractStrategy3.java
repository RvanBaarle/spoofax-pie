package mb.strategies;

import mb.sequences.Seq;

/**
 * Abstract class for a named strategy.
 *
 * Extend {@link AbstractStrategy3} if your strategy is named.
 * Create a lambda from {@link Strategy3} if your strategy is anonymous
 * (or name the lambda using {@link Strategy3#withName}).
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <A2> the type of the second argument (contravariant)
 * @param <A3> the type of the third argument (contravariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
public abstract class AbstractStrategy3<CTX, A1, A2, A3, T, R> implements Strategy3<CTX, A1, A2, A3, T, R> {

    @Override public abstract Seq<R> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, T input);

    @Override public abstract String getName();

    @Override public boolean isAnonymous() { return false; }

    @Override public void writeTo(StringBuilder sb) { sb.append(getName()); }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        writeTo(sb);
        return sb.toString();
    }
}
