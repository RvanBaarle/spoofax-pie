package mb.strategies;

import mb.sequences.Seq;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy<CTX, I, O> extends StrategyDecl {

    /**
     * Defines a named Strategy with two arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, I, O> Strategy<CTX, I, O> define(String name, Supplier<Strategy<CTX, I, O>> body) {
        return define(name, (ctx, input) -> body.get().eval(ctx, input));
    }

    // ---

    /**
     * Defines a named Strategy with two arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, I, O> Strategy<CTX, I, O> define(String name, Strategy<CTX, I, O> body) {
        return new Strategy<CTX, I, O>() {
            @Override public Seq<O> eval(CTX ctx, I input) {
                return body.eval(ctx, input);
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

    @Override
    default int getArity() { return 0; }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, I input);
}
