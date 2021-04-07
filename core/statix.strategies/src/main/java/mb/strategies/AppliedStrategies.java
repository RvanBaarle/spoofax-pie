package mb.strategies;

import mb.sequences.Seq;

/**
 * Strategies that are partially applied.
 */
/* package private */ final class AppliedStrategies {
    private AppliedStrategies() {}

    public final static class ApplStrategy1To0<CTX, A1, I, O> extends AbstractStrategy<CTX, I, O> {

        private final Strategy1<CTX, A1, I, O> strategy;
        private final A1 arg1;

        public ApplStrategy1To0(Strategy1<CTX, A1, I, O> strategy, A1 arg1) {
            this.strategy = strategy;
            this.arg1 = arg1;
        }

        @Override protected Seq<O> innerEval(CTX ctx, I input) {
            return strategy.eval(ctx, arg1, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(')');
        }
    }

    public final static class ApplStrategy2To0<CTX, A1, A2, I, O> extends AbstractStrategy<CTX, I, O> {

        private final Strategy2<CTX, A1, A2, I, O> strategy;
        private final A1 arg1;
        private final A2 arg2;

        public ApplStrategy2To0(Strategy2<CTX, A1, A2, I, O> strategy, A1 arg1, A2 arg2) {
            this.strategy = strategy;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override protected Seq<O> innerEval(CTX ctx, I input) {
            return strategy.eval(ctx, arg1, arg2, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                case 1: return this.strategy.getParamName(1);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(", ");
            strategy.writeArg(sb, 2, arg2);
            sb.append(')');
        }
    }

    public final static class ApplStrategy3To0<CTX, A1, A2, A3, I, O> extends AbstractStrategy<CTX, I, O> {

        private final Strategy3<CTX, A1, A2, A3, I, O> strategy;
        private final A1 arg1;
        private final A2 arg2;
        private final A3 arg3;

        public ApplStrategy3To0(Strategy3<CTX, A1, A2, A3, I, O> strategy, A1 arg1, A2 arg2, A3 arg3) {
            this.strategy = strategy;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        @Override protected Seq<O> innerEval(CTX ctx, I input) {
            return strategy.eval(ctx, arg1, arg2, arg3, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                case 1: return this.strategy.getParamName(1);
                case 2: return this.strategy.getParamName(2);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(", ");
            strategy.writeArg(sb, 2, arg2);
            sb.append(", ");
            strategy.writeArg(sb, 3, arg3);
            sb.append(')');
        }
    }

    // --- //

    public final static class ApplStrategy2To1<CTX, A1, A2, I, O> extends AbstractStrategy1<CTX, A2, I, O> {

        private final Strategy2<CTX, A1, A2, I, O> strategy;
        private final A1 arg1;

        public ApplStrategy2To1(Strategy2<CTX, A1, A2, I, O> strategy, A1 arg1) {
            this.strategy = strategy;
            this.arg1 = arg1;
        }

        @Override public Strategy<CTX, I, O> apply(A2 arg2) {
            // Delegate to the inner strategy, to avoid wrapping twice
            return strategy.apply(arg1, arg2);
        }

        @Override protected Seq<O> innerEval(CTX ctx, A2 arg2, I input) {
            return strategy.eval(ctx, arg1, arg2, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(')');
        }
    }

    public final static class ApplStrategy3To1<CTX, A1, A2, A3, I, O> extends AbstractStrategy1<CTX, A3, I, O> {

        private final Strategy3<CTX, A1, A2, A3, I, O> strategy;
        private final A1 arg1;
        private final A2 arg2;

        public ApplStrategy3To1(Strategy3<CTX, A1, A2, A3, I, O> strategy, A1 arg1, A2 arg2) {
            this.strategy = strategy;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override public Strategy<CTX, I, O> apply(A3 arg3) {
            // Delegate to the inner strategy, to avoid wrapping twice
            return strategy.apply(arg1, arg2, arg3);
        }

        @Override protected Seq<O> innerEval(CTX ctx, A3 arg3, I input) {
            return strategy.eval(ctx, arg1, arg2, arg3, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                case 1: return this.strategy.getParamName(1);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(", ");
            strategy.writeArg(sb, 2, arg2);
            sb.append(')');
        }
    }

    // --- //

    public final static class ApplStrategy3To2<CTX, A1, A2, A3, I, O> extends AbstractStrategy2<CTX, A2, A3, I, O> {

        private final Strategy3<CTX, A1, A2, A3, I, O> strategy;
        private final A1 arg1;

        public ApplStrategy3To2(Strategy3<CTX, A1, A2, A3, I, O> strategy, A1 arg1) {
            this.strategy = strategy;
            this.arg1 = arg1;
        }

        @Override
        public Strategy<CTX, I, O> apply(A2 arg2, A3 arg3) {
            // Delegate to the inner strategy, to avoid wrapping twice
            return strategy.apply(arg1, arg2, arg3);
        }

        @Override
        public Strategy1<CTX, A3, I, O> apply(A2 arg2) {
            // Delegate to the inner strategy, to avoid wrapping twice
            return strategy.apply(arg1, arg2);
        }

        @Override protected Seq<O> innerEval(CTX ctx, A2 arg2, A3 arg3, I input) {
            return strategy.eval(ctx, arg1, arg2, arg3, input);
        }

        @Override public String getName() { return strategy.getName(); }

        @Override
        public String getParamName(int index) {
            switch (index) {
                case 0: return this.strategy.getParamName(0);
                default: return super.getParamName(index);
            }
        }

        @Override public boolean isAnonymous() { return true; }

        @Override public void writeTo(StringBuilder sb) {
            sb.append(getName());
            sb.append('(');
            strategy.writeArg(sb, 1, arg1);
            sb.append(')');
        }
    }
}
