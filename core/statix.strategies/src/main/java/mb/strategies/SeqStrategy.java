package mb.strategies;

import mb.sequences.Sequence;

/**
 * The s1; s2; ...; sn strategy.
 */
public final class SeqStrategy<CTX, I, M, O> implements Strategy2<CTX, Strategy<CTX, I, M>, Strategy<CTX, M, O>, I, O> {

    @SuppressWarnings("rawtypes")
    private static final SeqStrategy instance = new SeqStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, M, O> SeqStrategy<CTX, I, M, O> getInstance() { return (SeqStrategy<CTX, I, M, O>)instance; }

    @Override
    public String getName() { return "seq"; }

    @Override
    public Sequence<O> eval(CTX ctx, Strategy<CTX, I, M> s1, Strategy<CTX, M, O> s2, I input) throws InterruptedException {
        return s1.eval(ctx, input).flatMap(it -> s2.eval(ctx, it));
    }

    /**
     * A builder for sequences of strategies.
     *
     * @param <I> the input type
     * @param <M> the output type
     */
    @SuppressWarnings("unused")
    public static class Builder<CTX, I, M> {

        private final Strategy<CTX, I, M> s;

        public Builder(Strategy<CTX, I, M> s) {
            this.s = s;
        }

        public <O> Builder<CTX, I, O> $(Strategy<CTX, M, O> s) {
            return new Builder<>(SeqStrategy.<CTX, I, M, O>getInstance().apply(this.s, s));
        }

        public Strategy<CTX, I, M> $() {
            return s;
        }

    }

}
