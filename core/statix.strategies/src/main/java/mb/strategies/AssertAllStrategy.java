package mb.strategies;

import mb.sequences.Sequence;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Asserts that the value conforms to the given predicate.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class AssertAllStrategy<CTX, I, O> implements Strategy2<CTX, Predicate<Sequence<O>>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final AssertAllStrategy instance = new AssertAllStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> AssertAllStrategy<CTX, I, O> getInstance() { return (AssertAllStrategy<CTX, I, O>)instance; }

    private AssertAllStrategy() {}

    @Override
    public String getName() { return "assertAll"; }

    @Override
    public Sequence<O> eval(
        CTX ctx,
        Predicate<Sequence<O>> predicate,
        Strategy<CTX, I, O> strategy,
        I input
    ) throws InterruptedException {
        Sequence<O> output = strategy.eval(ctx, input);
        if (!predicate.test(output)) return Sequence.empty();
        return output;
    }

}
