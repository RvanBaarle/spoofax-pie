package mb.strategies;

import mb.sequences.Seq;

import static mb.strategies.Strategies.*;

/**
 * Applies a strategy until it fails.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class RepeatStrategy<CTX, T> extends AbstractStrategy1<CTX, Strategy<CTX, T, T>, T, T> {

    @SuppressWarnings("rawtypes")
    private static final RepeatStrategy instance = new RepeatStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> RepeatStrategy<CTX, T> getInstance() { return (RepeatStrategy<CTX, T>)instance; }

    private RepeatStrategy() {}

    @Override
    public Seq<T> eval(CTX ctx, Strategy<CTX, T, T> s, T input) {
        // = try(s ; repeat(s))
        // = rec x : try(s ; x)
        return Strategies.<CTX, T, T>rec(x -> try_(seq(s).$(x).$())).eval(ctx, input);
    }

    @Override
    public String getName() {
        return "repeat";
    }
}
