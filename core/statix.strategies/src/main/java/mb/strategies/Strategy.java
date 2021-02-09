package mb.strategies;

@FunctionalInterface
public interface Strategy<CTX, I, O> extends StrategyDecl<CTX, O> {

    O eval(CTX ctx, I input) throws InterruptedException;

//    default String getName() { return this.getClass().getSimpleName(); }

    default Computation<CTX, O> apply(I input) {
        return (ctx) -> Strategy.this.eval(ctx, input);
    }
}
