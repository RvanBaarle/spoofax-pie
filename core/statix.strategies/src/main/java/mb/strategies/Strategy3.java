package mb.strategies;

import mb.sequences.Function3;
import mb.sequences.Seq;

import java.io.IOException;

/**
 * A strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <A1> the type of the first argument (contravariant)
 * @param <A2> the type of the second argument (contravariant)
 * @param <A3> the type of the third argument (contravariant)
 * @param <I> the type of input (contravariant)
 * @param <O> the type of output (covariant)
 */
@SuppressWarnings("Convert2Diamond") @FunctionalInterface
public interface Strategy3<CTX, A1, A2, A3, I, O> extends StrategyDecl {

    /**
     * Defines a named Strategy with three arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy3<CTX, A1, A2, A3, I, O> define(String name, Function3<A1, A2, A3, Strategy<CTX, I, O>> body) {
        return define(name, (ctx, p1, p2, p3, input) -> body.apply(p1, p2, p3).eval(ctx, input));
    }

    /**
     * Defines a named Strategy with two arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy2<CTX, A2, A3, I, O> define(String name, Function3<A1, A2, A3, Strategy<CTX, I, O>> body, A1 arg1) {
        return define(name, (ctx, p1, p2, p3, input) -> body.apply(p1, p2, p3).eval(ctx, input), arg1);
    }

    /**
     * Defines a named Strategy with one argument.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy1<CTX, A3, I, O> define(String name, Function3<A1, A2, A3, Strategy<CTX, I, O>> body, A1 arg1, A2 arg2) {
        return define(name, (ctx, p1, p2, p3, input) -> body.apply(p1, p2, p3).eval(ctx, input), arg1, arg2);
    }

    /**
     * Defines a named Strategy with no arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy<CTX, I, O> define(String name, Function3<A1, A2, A3, Strategy<CTX, I, O>> body, A1 arg1, A2 arg2, A3 arg3) {
        return define(name, (ctx, p1, p2, p3, input) -> body.apply(p1, p2, p3).eval(ctx, input), arg1, arg2, arg3);
    }

    // ---

    /**
     * Defines a named Strategy with three arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy3<CTX, A1, A2, A3, I, O> define(String name, Strategy3<CTX, A1, A2, A3, I, O> body) {
        return new Strategy3<CTX, A1, A2, A3, I, O>() {
            @Override public Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) {
                return body.eval(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public Strategy<CTX, I, O> apply(A1 arg1, A2 arg2, A3 arg3) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                final Strategy<CTX, I, O> partiallyApplied = body.apply(arg1, arg2, arg3);
                return partiallyApplied.isAnonymous() ? Strategy.define(name, partiallyApplied) : partiallyApplied;
            }

            @Override
            public Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                final Strategy1<CTX, A3, I, O> partiallyApplied = body.apply(arg1, arg2);
                return partiallyApplied.isAnonymous() ? Strategy1.define(name, partiallyApplied) : partiallyApplied;
            }

            @Override
            public Strategy2<CTX, A2, A3, I, O> apply(A1 arg1) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                final Strategy2<CTX, A2, A3, I, O> partiallyApplied = body.apply(arg1);
                return partiallyApplied.isAnonymous() ? Strategy2.define(name, partiallyApplied) : partiallyApplied;
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
     * Defines a named Strategy with two arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy2<CTX, A2, A3, I, O> define(String name, Strategy3<CTX, A1, A2, A3, I, O> body, A1 arg1) {
        return new Strategy2<CTX, A2, A3, I, O>() {
            @Override public Seq<O> eval(CTX ctx, A2 arg2, A3 arg3, I input) {
                return body.eval(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public Strategy<CTX, I, O> apply(A2 arg2, A3 arg3) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return body.apply(arg1, arg2, arg3);
            }

            @Override
            public Strategy1<CTX, A3, I, O> apply(A2 arg2) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return body.apply(arg1, arg2);
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

    /**
     * Defines a named Strategy with one argument.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy1<CTX, A3, I, O> define(String name, Strategy3<CTX, A1, A2, A3, I, O> body, A1 arg1, A2 arg2) {
        return new Strategy1<CTX, A3, I, O>() {
            @Override public Seq<O> eval(CTX ctx, A3 arg3, I input) {
                return body.eval(ctx, arg1, arg2, arg3, input);
            }

            @Override
            public Strategy<CTX, I, O> apply(A3 arg3) {
                // Direct to another implementation,
                // to avoid wrapping the strategy twice.
                return body.apply(arg1, arg2, arg3);
            }

            @Override public String getName() { return name; }

            @Override public boolean isAnonymous() { return true; }

            @Override public <A extends Appendable> A write(A buffer) throws IOException {
                buffer.append(getName());
                buffer.append('(');
                buffer.append(arg1.toString());
                buffer.append(", ");
                buffer.append(arg2.toString());
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

    /**
     * Defines a named Strategy with no arguments.
     *
     * @param name the strategy name
     * @param body the strategy body
     * @param <CTX> the type of context (invariant)
     * @param <A1> the type of the first argument (contravariant)
     * @param <A2> the type of the second argument (contravariant)
     * @param <A3> the type of the third argument (contravariant)
     * @param <I> the type of input (contravariant)
     * @param <O> the type of output (covariant)
     * @return the strategy
     */
    static <CTX, A1, A2, A3, I, O> Strategy<CTX, I, O> define(String name, Strategy3<CTX, A1, A2, A3, I, O> body, A1 arg1, A2 arg2, A3 arg3) {
        return new Strategy<CTX, I, O>() {
            @Override public Seq<O> eval(CTX ctx, I input) {
                return body.eval(ctx, arg1, arg2, arg3, input);
            }

            @Override public String getName() { return name; }

            @Override public boolean isAnonymous() { return true; }

            @Override public <A extends Appendable> A write(A buffer) throws IOException {
                buffer.append(getName());
                buffer.append('(');
                buffer.append(arg1.toString());
                buffer.append(", ");
                buffer.append(arg2.toString());
                buffer.append(", ");
                buffer.append(arg3.toString());
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
    default int getArity() { return 3; }

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @return the resulting partially applied strategy
     */
    default Strategy2<CTX, A2, A3, I, O> apply(A1 arg1) {
        return define(Strategy3.this.getName(), Strategy3.this, arg1);
    }

    /**
     * Partially applies the strategy, providing the first and second arguments.
     *
     * As an optimization, partially applying the returned strategy
     * will not wrap the strategy twice.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the resulting partially applied strategy
     */
    default Strategy1<CTX, A3, I, O> apply(A1 arg1, A2 arg2) {
        return define(Strategy3.this.getName(), Strategy3.this, arg1, arg2);
    }

    /**
     * Partially applies the strategy, providing the first, second, and third arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @return the resulting partially applied strategy
     */
    default Strategy<CTX, I, O> apply(A1 arg1, A2 arg2, A3 arg3) {
        return define(Strategy3.this.getName(), Strategy3.this, arg1, arg2, arg3);
    }

    /**
     * Applies the strategy to the given arguments.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param input the input value
     * @return a lazy sequence of results
     */
    Seq<O> eval(CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input);
}
