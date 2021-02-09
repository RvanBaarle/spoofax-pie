package mb.strategies;

/**
 * A (partially) applied strategy.
 */
public interface StrategyApplDecl extends StrategyDecl {

    /**
     * Writes the arguments of this strategy to the specified buffer.
     *
     * @param buffer the buffer to write to
     * @return the buffer
     */
    @SuppressWarnings("UnusedReturnValue")
    StringBuilder writeArgs(StringBuilder buffer);

}
