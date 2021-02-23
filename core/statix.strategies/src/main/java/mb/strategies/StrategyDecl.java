package mb.strategies;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A strategy declaration.
 */
public interface StrategyDecl extends Writable {

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

    /**
     * Gets whether this is an atom strategy.
     *
     * @return {@code true} when this is an atom strategy;
     * otherwise, {@code false} when this is a binary strategy
     */
    default boolean isAtom() { return true; }

    /**
     * Writes the specified argument to the specified {@link StringBuilder}.
     *
     * @param sb the {@link StringBuilder} to write to
     * @param index the one-based index of the argument to write
     * @param arg the argument to write
     */
    default void writeArg(StringBuilder sb, int index, Object arg) {
        if (index < 1 || index > getArity()) throw new ArrayIndexOutOfBoundsException();

        if (arg instanceof Writable) {
            ((Writable)arg).writeTo(sb);
        } else if (arg instanceof Predicate) {
            sb.append("<predicate>");
        } else if (arg instanceof BiPredicate) {
            sb.append("<bipredicate>");
        } else if (arg instanceof Function) {
            sb.append("<function>");
        } else if (arg instanceof BiFunction) {
            sb.append("<bifunction>");
        } else if (arg instanceof Consumer) {
            sb.append("<consumer>");
        } else if (arg instanceof Supplier) {
            sb.append("<supplier>");
        } else if (arg instanceof Class) {
            sb.append(((Class<?>)arg).getSimpleName());
        } else {
            sb.append(arg.toString());
        }
    }

}
