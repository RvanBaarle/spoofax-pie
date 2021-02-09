package mb.strategies;

@FunctionalInterface
public interface Strategy2<CTX, A1, A2, I, O> extends StrategyDecl<CTX, O> {

    O eval(CTX ctx, A1 arg1, A2 arg2, I input) throws InterruptedException;

//    default String getName() { return this.getClass().getSimpleName(); }

    default Strategy1<CTX, A2, I, O> apply(A1 arg1) {
        return (ctx, arg2, input) -> Strategy2.this.eval(ctx, arg1, arg2, input);
    }
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2) {
        return (ctx, input) -> Strategy2.this.eval(ctx, arg1, arg2, input);
    }
    default Computation<CTX, O> apply(A1 arg1, A2 arg2, I input) {
        return (ctx) -> Strategy2.this.eval(ctx, arg1, arg2, input);
    }

}
