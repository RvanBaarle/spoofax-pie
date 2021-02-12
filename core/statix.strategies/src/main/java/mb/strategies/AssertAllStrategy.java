package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Predicate;

/**
 * Asserts that the value conforms to the given predicate.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class AssertAllStrategy<CTX, I, O> implements Strategy2<CTX, Predicate<Seq<O>>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final AssertAllStrategy instance = new AssertAllStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> AssertAllStrategy<CTX, I, O> getInstance() { return (AssertAllStrategy<CTX, I, O>)instance; }

    private AssertAllStrategy() {}

    @Override
    public String getName() { return "assertAll"; }

    @Override
    public Seq<O> eval(
        CTX ctx,
        Predicate<Seq<O>> predicate,
        Strategy<CTX, I, O> strategy,
        I input
    ) {
        Seq<O> output = strategy.eval(ctx, input);
        if (!predicate.test(output)) return Seq.empty();
        return output;
    }

}
