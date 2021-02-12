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

    /**
     * Gets the arity of the strategy.
     *
     * The arity of a basic strategy {@code I -> O} is 0.
     *
     * @return the arity of the strategy, excluding the input argument
     */
    int getArity();

}
