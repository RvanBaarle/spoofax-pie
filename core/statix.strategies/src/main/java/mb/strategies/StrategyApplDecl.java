package mb.strategies;

public interface StrategyApplDecl<CTX, O> extends StrategyDecl<CTX, O> {

    StringBuilder writeArgs(StringBuilder buffer);

}
