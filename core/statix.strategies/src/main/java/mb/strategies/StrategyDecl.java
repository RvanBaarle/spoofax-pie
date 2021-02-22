package mb.strategies;

import java.io.IOException;

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

    /**
     * Gets whether this strategy is anonymous.
     *
     * A strategy is anonymous when it was created from a lambda or closure,
     * or when it is the application of a strategy.
     *
     * @return {@code true} when this strategy is anonymous;
     * otherwise, {@code false}
     */
    default boolean isAnonymous() { return true; }

    /**
     * Gets the precedence of this strategy relative to other strategies.
     *
     * @return the precedence, higher means higher precedence
     */
    default int getPrecedence() { return Integer.MAX_VALUE; }

}
