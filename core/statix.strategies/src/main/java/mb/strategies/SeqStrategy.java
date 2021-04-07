package mb.strategies;

import mb.sequences.Seq;

/**
 * The s1; s2; ...; sn strategy.
 */
@SuppressWarnings("Convert2Diamond")
public final class SeqStrategy<CTX, I, M, O> extends AbstractStrategy2<CTX, Strategy<CTX, I, M>, Strategy<CTX, M, O>, I, O> {

    @SuppressWarnings("rawtypes")
    private static final SeqStrategy instance = new SeqStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, M, O> SeqStrategy<CTX, I, M, O> getInstance() { return (SeqStrategy<CTX, I, M, O>)instance; }

    private SeqStrategy() {}

    @Override
    public String getName() { return "seq"; }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s1";
            case 1: return "s2";
            default: return super.getParamName(index);
        }
    }

    @Override
    public Strategy<CTX, I, O> apply(Strategy<CTX, I, M> s1, Strategy<CTX, M, O> s2) {
        return new AbstractStrategy<CTX, I, O>() {
            @Override
            public String getName() { return "seq"; }

            @Override
            public String getParamName(int index) {
                switch (index) {
                    case 0: return "s1";
                    case 1: return "s2";
                    default: return super.getParamName(index);
                }
            }

            @Override
            public int getPrecedence() {
                return 0;
            }

            @Override
            public boolean isAtom() { return false; }

            @Override
            protected Seq<O> innerEval(CTX ctx, I input) {
                return s1.eval(ctx, input).flatMap(it -> s2.eval(ctx, it));
            }

            @Override
            public void writeTo(StringBuilder sb) {
                final Associativity associativity = Associativity.Left;
                StrategyPP.writeLeft(sb, s1, getPrecedence(), associativity);
                sb.append("; ");
                StrategyPP.writeRight(sb, s2, getPrecedence(), associativity);
            }
        };
    }

    @Override
    protected Seq<O> innerEval(CTX ctx, Strategy<CTX, I, M> s1, Strategy<CTX, M, O> s2, I input) {
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
