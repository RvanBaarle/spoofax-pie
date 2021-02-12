package mb.strategies;

import mb.sequences.InterruptibleIterator;
import mb.sequences.InterruptibleIteratorBase;
import mb.sequences.Seq;

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

    private GlcStrategy() {}

    @Override
    public String getName() { return "glc"; }

    @Override
    public Seq<O> apply(
        CTX ctx,
        Strategy<CTX, I, M> condition,
        Strategy<CTX, M, O> onSuccess,
        Strategy<CTX, I, O> onFailure,
        I input
    ) {
        final Seq<M> conditionSeq = condition.apply(ctx, input);
        final Seq<O> onSuccessSeq = conditionSeq.flatMap(it -> onSuccess.apply(ctx, it));
        final Seq<O> onFailureSeq = onFailure.apply(ctx, input);
        return () -> new InterruptibleIteratorBase<O>() {
            InterruptibleIterator<O> seqIter = null;

            @Override
            protected void computeNext() throws InterruptedException {
                if(this.seqIter == null) {
                    if(Boolean.TRUE.equals(conditionSeq.any().tryEval())) {
                        this.seqIter = onSuccessSeq.iterator();
                    } else {
                        this.seqIter = onFailureSeq.iterator();
                    }
                }
                // Yield the elements from this.seq
                if (this.seqIter.hasNext()) {
                    setNext(this.seqIter.next());
                } else {
                    finished();
                }
            }
        };
    }

}
