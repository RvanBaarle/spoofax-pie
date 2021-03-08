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
     * @return the buffer
     */
    public static void writeLeft(StringBuilder buffer, StrategyDecl strategy, int precedence, Associativity associativity) {
        boolean parenthesize = !strategy.isAtom() && (strategy.getPrecedence() < precedence
            || (strategy.getPrecedence() == precedence && associativity == Associativity.Right));
        if (parenthesize) buffer.append('(');
        buffer.append(strategy);
        if (parenthesize) buffer.append(')');
    }

    /**
     * Writes a right strategy expression argument to the specified buffer, optionally wrapping it in parentheses.
     *
     * @param buffer the buffer to write to
     * @param strategy the strategy to write
     * @param precedence the precedence of the parent strategy; a higher value indicates a higher precedence
     * @param associativity the associativity of the parent strategy
     * @return the buffer
     */
    public static void writeRight(StringBuilder buffer, StrategyDecl strategy, int precedence, Associativity associativity) {
        boolean parenthesize = !strategy.isAtom() && (strategy.getPrecedence() < precedence
            || (strategy.getPrecedence() == precedence && associativity == Associativity.Left));
        if (parenthesize) buffer.append('(');
        buffer.append(strategy);
        if (parenthesize) buffer.append(')');
    }


    /**
     * Writes a middle (or only) strategy expression argument to the specified buffer, optionally wrapping it in parentheses.
     *
     * @param buffer the buffer to write to
     * @param strategy the strategy to write
     * @param precedence the precedence of the parent strategy; a higher value indicates a higher precedence
     * @return the buffer
     */
    public static void writeMiddle(StringBuilder buffer, StrategyDecl strategy, int precedence) {
        boolean parenthesize = !strategy.isAtom() && strategy.getPrecedence() < precedence;
        if (parenthesize) buffer.append('(');
        buffer.append(strategy);
        if (parenthesize) buffer.append(')');
    }
}