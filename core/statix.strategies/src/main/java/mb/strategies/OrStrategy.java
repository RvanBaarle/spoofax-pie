package mb.strategies;

import mb.sequences.Sequence;

/**
 * Disjunction.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class OrStrategy<CTX, I, O> implements Strategy2<CTX, Strategy<CTX, I, O>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final OrStrategy instance = new OrStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> OrStrategy<CTX, I, O> getInstance() { return (OrStrategy<CTX, I, O>)instance; }

    private OrStrategy() {}

    @Override
    public String getName() { return "or"; }

    @Override
    public Sequence<O> eval(
        CTX ctx,
        Strategy<CTX, I, O> s1,
        Strategy<CTX, I, O> s2,
        I input
    ) throws InterruptedException {
        final Sequence<O> results1 = s1.eval(ctx, input);
        final Sequence<O> results2 = s2.eval(ctx, input);
        return results1.concatWith(results2);
    }

}