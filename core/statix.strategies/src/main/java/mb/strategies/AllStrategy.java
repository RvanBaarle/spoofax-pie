package mb.strategies;

import mb.sequences.ComputingInterruptibleIterator;
import mb.sequences.InterruptibleIterator;
import mb.sequences.Seq;

import java.util.function.Predicate;

/**
 * Asserts that the results all match the given predicate.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class AllStrategy<CTX, I, O> implements Strategy2<CTX, Predicate<O>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final AllStrategy instance = new AllStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> AllStrategy<CTX, I, O> getInstance() { return (AllStrategy<CTX, I, O>)instance; }

    private AllStrategy() {}

    @Override
    public String getName() { return "all"; }

    @Override
    public boolean isAnonymous() { return false; }

    @Override
    public Seq<O> eval(
        CTX ctx,
        Predicate<O> predicate,
        Strategy<CTX, I, O> strategy,
        I input
    ) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() { return getName(); }

}
