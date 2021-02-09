package mb.strategies;

public interface Computation<CTX, O> extends StrategyDecl<CTX, O> {

    O eval(CTX ctx) throws InterruptedException;

//    default String getName() { return this.getClass().getSimpleName(); }

}
