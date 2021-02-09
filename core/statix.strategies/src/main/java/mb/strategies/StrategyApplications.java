package mb.strategies;

final class AppliedStrategy2<CTX, A1, A2, A3, I, O> implements Strategy2<CTX, A2, A3, I, O>, StrategyApplDecl<CTX, O> {
    private final Strategy3<CTX, A1, A2, A3, I, O> s;
    private final A1 arg1;

    public AppliedStrategy2(Strategy3<CTX, A1, A2, A3, I, O> s, A1 arg1) {
        this.s = s;
        this.arg1 = arg1;
    }

    @Override
    public O eval(CTX ctx, A2 arg2, A3 arg3, I input) throws InterruptedException {
        return s.eval(ctx, arg1, arg2, arg3, input);
    }

    @Override
    public String getName() {
        return s.getName();
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(s.getName());
        sb.append("(");
        writeArgs(sb);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public StringBuilder writeArgs(StringBuilder buffer) {
        buffer.append(arg1.toString());
        if (s instanceof StrategyApplDecl) {
            buffer.append(", ");
            ((StrategyApplDecl<?, ?>)s).writeArgs(buffer);
        }
        return buffer;
    }
}

final class AppliedStrategy1<CTX, A1, A2, I, O> implements Strategy1<CTX, A2, I, O>, StrategyApplDecl<CTX, O> {
    private final Strategy2<CTX, A1, A2, I, O> s;
    private final A1 arg1;

    public AppliedStrategy1(Strategy2<CTX, A1, A2, I, O> s, A1 arg1) {
        this.s = s;
        this.arg1 = arg1;
    }

    @Override
    public O eval(CTX ctx, A2 arg2, I input) throws InterruptedException {
        return s.eval(ctx, arg1, arg2, input);
    }

    @Override
    public String getName() {
        return s.getName();
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(s.getName());
        sb.append("(");
        writeArgs(sb);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public StringBuilder writeArgs(StringBuilder buffer) {
        buffer.append(arg1.toString());
        if (s instanceof StrategyApplDecl) {
            buffer.append(", ");
            ((StrategyApplDecl<?, ?>)s).writeArgs(buffer);
        }
        return buffer;
    }
}

final class AppliedStrategy<CTX, A1, I, O> implements Strategy<CTX, I, O>, StrategyApplDecl<CTX, O> {
    private final Strategy1<CTX, A1, I, O> s;
    private final A1 arg1;

    public AppliedStrategy(Strategy1<CTX, A1, I, O> s, A1 arg1) {
        this.s = s;
        this.arg1 = arg1;
    }

    @Override
    public O eval(CTX ctx, I input) throws InterruptedException {
        return s.eval(ctx, arg1, input);
    }

    @Override
    public String getName() {
        return s.getName();
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(s.getName());
        sb.append("(");
        writeArgs(sb);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public StringBuilder writeArgs(StringBuilder buffer) {
        buffer.append(arg1.toString());
        if (s instanceof StrategyApplDecl) {
            buffer.append(", ");
            ((StrategyApplDecl<?, ?>)s).writeArgs(buffer);
        }
        return buffer;
    }
}

final class AppliedComputation<CTX, I, O> implements Computation<CTX, O> {
    private final Strategy<CTX, I, O> s;
    private final I input;

    public AppliedComputation(Strategy<CTX, I, O> s, I input) {
        this.s = s;
        this.input = input;
    }

    @Override
    public O eval(CTX ctx) throws InterruptedException {
        return s.eval(ctx, input);
    }

    @Override
    public String getName() {
        return s.getName();
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(s.getName());
        if (s instanceof StrategyApplDecl) {
            sb.append("(");
            ((StrategyApplDecl<?, ?>)s).writeArgs(sb);
            sb.append(")");
        }
        sb.append("> ");
        sb.append(input.toString());
        return sb.toString();
    }

}
