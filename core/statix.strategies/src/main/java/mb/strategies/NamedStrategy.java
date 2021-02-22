package mb.strategies;

/**
 * A named strategy.
 */
public interface NamedStrategy extends StrategyDecl {

    /**
     * Gets the name of the strategy.
     *
     * @return the name of the strategy
     */
    String getName();

//    /**
//     * Gets the name of the strategy.
//     *
//     * @return the name of the strategy
//     */
//    default String getName() { return this.getClass().getSimpleName(); }


}
