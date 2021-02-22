package mb.strategies;

import mb.sequences.Seq;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Evaluates a strategy and prints both its entrance and its results.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class DebugStrategy<CTX, I, O> implements Strategy2<CTX, Function<O, String>, Strategy<CTX, I, O>, I, O>{

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
    public Seq<O> eval(CTX ctx, Function<O, String> transform, Strategy<CTX, I, O> strategy, I input) {
        Seq<O> results = strategy.eval(ctx, input);
        System.out.print("▶ " + strategy + ": ");
        return results.debug(transform);
    }

    @Override
    public String toString() { return getName(); }

}
