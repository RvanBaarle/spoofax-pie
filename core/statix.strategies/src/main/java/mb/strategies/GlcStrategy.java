package mb.strategies;

import mb.sequences.Sequence;

/**
 * Guarded left choice.
 *
 * Note: buffer the sequence returned by the {@code condition} strategy to avoid double computation of the first element.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <M> the type of intermediates
 * @param <O> the type of outputs
 */
public final class GlcStrategy<CTX, I, M, O> implements Strategy3<CTX, Strategy<CTX, I, M>, Strategy<CTX, M, O>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final GlcStrategy instance = new GlcStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, M, O> GlcStrategy<CTX, I, M, O> getInstance() { return (GlcStrategy<CTX, I, M, O>)instance; }

    @Override
    public String getName() { return "glc"; }

    @Override
    public Sequence<O> eval(
        CTX ctx,
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure,
        I input
    ) throws InterruptedException {
        // If `condition` is an expensive operation, the sequence should be buffered.
        // We don't do that here, to give users the flexibility to do this on the calling site.
        final Sequence<M> values = condition.eval(ctx, input);
        if (values.any()) {
            return values.flatMap(it -> onSuccess.eval(ctx, it));
        } else {
            return onFailure.eval(ctx, input);
        }
    }

}
