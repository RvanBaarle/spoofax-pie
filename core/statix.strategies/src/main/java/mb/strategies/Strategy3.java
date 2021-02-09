package mb.strategies;

@FunctionalInterface
public interface Strategy3<CTX, A1, A2, A3, I, O> extends StrategyDecl<CTX, O> {

    O eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) throws InterruptedException;

//    default String getName() { return this.getClass().getSimpleName(); }

    default Strategy2<CTX, A2, A3, I, O> apply(A1 arg1) {
        return new AppliedStrategy2<>(this, arg1);
//        return (ctx, arg2, arg3, input) -> Strategy3.this.eval(ctx, arg1, arg2, arg3, input);
    }
    default Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
        return new AppliedStrategy1<>(apply(arg1), arg2);
//        return (ctx, arg3, input) -> Strategy3.this.eval(ctx, arg1, arg2, arg3, input);
    }
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2, A3 arg3) {
        return new AppliedStrategy<>(apply(arg1).apply(arg2), arg3);
//        return (ctx, input) -> Strategy3.this.eval(ctx, arg1, arg2, arg3, input);
    }
    default Computation<CTX, O> apply(A1 arg1, A2 arg2, A3 arg3, I input) {
        return new AppliedComputation<>(apply(arg1).apply(arg2).apply(arg3), input);
//        return (ctx) -> Strategy3.this.eval(ctx, arg1, arg2, arg3, input);
    }
}
