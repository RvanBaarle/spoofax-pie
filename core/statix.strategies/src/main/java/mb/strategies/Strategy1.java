package mb.strategies;

@FunctionalInterface
public interface Strategy1<CTX, A1, I, O> extends StrategyDecl<CTX, O> {

    O eval(CTX ctx, A1 arg1, I input) throws InterruptedException;

//    default String getName() { return this.getClass().getSimpleName(); }

    default Strategy<CTX, I, O> apply(A1 arg1) {
        return (ctx, input) -> Strategy1.this.eval(ctx, arg1, input);
    }
    default Computation<CTX, O> apply(A1 arg1, I input) {
        return (ctx) -> Strategy1.this.eval(ctx, arg1, input);
    }
}
