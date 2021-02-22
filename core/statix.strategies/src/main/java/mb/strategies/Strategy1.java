package mb.strategies;

import mb.sequences.Seq;

import java.io.IOException;
import java.util.function.Function;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy1<CTX, A1, I, O> extends StrategyDecl {

    /**
     * Defines a named Strategy with one argument.
     *
     * @param name the strategy name
     * @param body a function producing the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, I, O> Strategy1<CTX, A1, I, O> define(String name, Function<A1, Strategy<CTX, I, O>> body) {
        return define(name, (ctx, p1, input) -> body.apply(p1).eval(ctx, input));
    }

    /**
     * Defines a named Strategy with one argument.
     *
     * @param name the strategy name
     * @param body a function producing the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, I, O> Strategy<CTX, I, O> define(String name, Function<A1, Strategy<CTX, I, O>> body, A1 arg1) {
        return define(name, (ctx, p1, input) -> body.apply(p1).eval(ctx, input), arg1);
    }

    // ---

    /**
     * Defines a named Strategy with one argument.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, I, O> Strategy1<CTX, A1, I, O> define(String name, Strategy1<CTX, A1, I, O> body) {
        return new Strategy1<CTX, A1, I, O>() {
            @Override public Seq<O> eval(CTX ctx, A1 arg1, I input) {
                return body.eval(ctx, arg1, input);
            }

            @Override
            public Strategy<CTX, I, O> apply(A1 arg1) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                final Strategy<CTX, I, O> partiallyApplied = body.apply(arg1);
                return partiallyApplied.isAnonymous() ? Strategy.define(name, partiallyApplied) : partiallyApplied;
            }

            @Override public String getName() { return name; }

            @Override public boolean isAnonymous() { return false; }

            @Override public <A extends Appendable> A write(A buffer) throws IOException {
                buffer.append(getName());
                return buffer;
            }

            @Override public String toString() {
                try {
                    return write(new StringBuilder()).toString();
                } catch(IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    /**
     * Defines a named Strategy with no arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, I, O> Strategy<CTX, I, O> define(String name, Strategy1<CTX, A1, I, O> body, A1 arg1) {
        return new Strategy<CTX, I, O>() {
            @Override public Seq<O> eval(CTX ctx, I input) {
                return body.eval(ctx, arg1, input);
            }

            @Override public String getName() { return name; }

            @Override public boolean isAnonymous() { return true; }

            @Override public <A extends Appendable> A write(A buffer) throws IOException {
                buffer.append(getName());
                buffer.append('(');
                buffer.append(arg1.toString());
                buffer.append(')');
                return buffer;
            }

            @Override public String toString() {
                try {
                    return write(new StringBuilder()).toString();
                } catch(IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }



    @Override
    default int getArity() { return 1; }

    /**
     * Partially applies the strategy, providing the first arguments.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1) {
        return define(Strategy1.this.getName(), Strategy1.this, arg1);
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, A1 arg1, I input);

}
