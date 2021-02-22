package mb.strategies;

import java.io.IOException;

public final class StrategyPP {
    private StrategyPP() {}

    /**
     * Writes a left strategy expression argument to the specified buffer, optionally wrapping it in parentheses.
     *
     * @param buffer the buffer to write to
     * @param strategy the strategy to write
     * @param precedence the precedence of the parent strategy; a higher value indicates a higher precedence
     * @param associativity the associativity of the parent strategy
     * @param <A> the type of buffer
     * @throws IOException if an I/O exception occurred
     */
    public static <A extends Appendable> void writeLeft(A buffer, StrategyDecl strategy, int precedence, Associativity associativity) throws IOException {
        boolean parenthesize = strategy.getPrecedence() > precedence
            || (strategy.getPrecedence() == precedence && associativity == Associativity.Right);
        if (parenthesize) buffer.append('(');
        strategy.write(buffer);
        if (parenthesize) buffer.append(')');
    }

    /**
     * Writes a right strategy expression argument to the specified buffer, optionally wrapping it in parentheses.
     *
     * @param buffer the buffer to write to
     * @param strategy the strategy to write
     * @param precedence the precedence of the parent strategy; a higher value indicates a higher precedence
     * @param associativity the associativity of the parent strategy
     * @param <A> the type of buffer
     * @throws IOException if an I/O exception occurred
     */
    public static <A extends Appendable> void writeRight(A buffer, StrategyDecl strategy, int precedence, Associativity associativity) throws IOException {
        boolean parenthesize = strategy.getPrecedence() > precedence
            || (strategy.getPrecedence() == precedence && associativity == Associativity.Left);
        if (parenthesize) buffer.append('(');
        strategy.write(buffer);
        if (parenthesize) buffer.append(')');
    }


    /**
     * Writes a middle (or only) strategy expression argument to the specified buffer, optionally wrapping it in parentheses.
     *
     * @param buffer the buffer to write to
     * @param strategy the strategy to write
     * @param precedence the precedence of the parent strategy; a higher value indicates a higher precedence
     * @param <A> the type of buffer
     * @throws IOException if an I/O exception occurred
     */
    public static <A extends Appendable> void writeMiddle(A buffer, StrategyDecl strategy, int precedence) throws IOException {
        boolean parenthesize = strategy.getPrecedence() > precedence;
        if (parenthesize) buffer.append('(');
        strategy.write(buffer);
        if (parenthesize) buffer.append(')');
    }
}
