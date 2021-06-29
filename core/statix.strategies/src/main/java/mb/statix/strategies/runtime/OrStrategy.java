package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.InterruptibleIteratorBase;
import mb.statix.sequences.Seq;
import mb.statix.strategies.NamedStrategy2;
import mb.statix.strategies.NamedStrategy3;
import mb.statix.strategies.Strategy;

/**
 * Disjunction strategy.
 *
 * This evaluates two strategies on the input, and returns the elements of the first sequence
 * and then the elements of the second sequence, but only if at least one succeeds.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input (contravariant)
 * @param <R> the type of output (covariant)
 */
public final class OrStrategy<CTX, T, R> extends NamedStrategy2<CTX, Strategy<CTX, T, R>, Strategy<CTX, T, R>, T, R> {

    @SuppressWarnings("rawtypes")
    private static final OrStrategy instance = new OrStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T, R> OrStrategy<CTX, T, R> getInstance() { return (OrStrategy<CTX, T, R>)instance; }

    private OrStrategy() { /* Prevent instantiation. Use getInstance(). */ }

    @Override
    public Seq<R> eval(CTX ctx, Strategy<CTX, T, R> s1, Strategy<CTX, T, R> s2, T input) {
        final Seq<R> s1Seq = s1.eval(ctx, input);
        final Seq<R> s2Seq = s2.eval(ctx, input);
        return s1Seq.concatWith(s2Seq);
    }

    @Override
    public String getName() {
        return "or";
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s1";
            case 1: return "s2";
            default: return super.getParamName(index);
        }
    }
}
