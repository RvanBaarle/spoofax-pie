package mb.strategies;

import mb.sequences.Seq;

/**
 * Abstract class for a named strategy.
 *
 * Extend {@link AbstractStrategy} if your strategy is named.
 * Create a lambda from {@link Strategy} if your strategy is anonymous
 * (or name the lambda using {@link Strategy#withName}).
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
public abstract class AbstractStrategy<CTX, T, R> implements Strategy<CTX, T, R> {

    @Override public final Seq<R> eval(CTX ctx, T input) {
        final StrategyEventHandler eventHandler = ctx instanceof Context ? ((Context)ctx).getEventHandler() : null;
        final T newInput = eventHandler != null ? eventHandler.enter(this, ctx, input) : input;
        Seq<R> output = innerEval(ctx, newInput);
        return eventHandler != null ? eventHandler.leave(this, ctx, output) : output;
    }

    protected abstract Seq<R> innerEval(CTX ctx, T input);

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
