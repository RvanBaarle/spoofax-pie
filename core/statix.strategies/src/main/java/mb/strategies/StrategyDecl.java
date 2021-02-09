package mb.strategies;

public interface StrategyDecl<CTX, O> {

    default String getName() { return this.getClass().getSimpleName(); }

}
