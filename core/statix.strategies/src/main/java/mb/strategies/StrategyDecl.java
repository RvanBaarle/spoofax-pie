package mb.strategies;

/**
 * A strategy declaration.
 */
public interface StrategyDecl {

    /**
     * Gets the name of the strategy.
     *
     * @return the name of the strategy
     */
    default String getName() { return this.getClass().getSimpleName(); }

}
