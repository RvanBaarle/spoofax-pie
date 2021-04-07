package mb.strategies;

import mb.sequences.Seq;

/**
 * Abstract class for a named strategy.
 *
 * Extend {@link AbstractStrategy1} if your strategy is named.
 * Create a lambda from {@link Strategy1} if your strategy is anonymous
 * (or name the lambda using {@link Strategy1#withName}).
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
public abstract class AbstractStrategy1<CTX, A1, T, R> implements Strategy1<CTX, A1, T, R> {

    @Override public final Seq<R> eval(CTX ctx, A1 arg1, T input) {
        final StrategyEventHandler eventHandler = ctx instanceof Context ? ((Context)ctx).getEventHandler() : null;
        final T newInput = eventHandler != null ? eventHandler.enter(this, ctx, arg1, input) : input;
        Seq<R> output = innerEval(ctx, arg1, newInput);
        return eventHandler != null ? eventHandler.leave(this, ctx, output) : output;
    }

    protected abstract Seq<R> innerEval(CTX ctx, A1 arg1, T input);

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
