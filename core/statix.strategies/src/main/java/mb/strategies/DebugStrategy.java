package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Consumer;

/**
 * Evaluates a strategy and applies an action to each element.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class DebugStrategy<CTX, I, O> implements Strategy2<CTX, Consumer<? super O>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final DebugStrategy instance = new DebugStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> DebugStrategy<CTX, I, O> getInstance() { return (DebugStrategy<CTX, I, O>)instance; }

    private DebugStrategy() {}

    @Override
    public String getName() { return "debug"; }

    @Override
    public boolean isAnonymous() { return false; }

    @Override
    public Seq<O> eval(
        CTX ctx,
        Consumer<? super O> action,
        Strategy<CTX, I, O> strategy,
        I input
    ) {
        return strategy.eval(ctx, input).map(it -> { action.accept(it); return it; });
    }

    @Override
    public String toString() { return getName(); }

}
