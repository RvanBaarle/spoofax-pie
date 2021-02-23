package mb.strategies;

import mb.sequences.Seq;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Evaluates a strategy and prints both its entrance and its results.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class DebugStrategy<CTX, I, O> extends AbstractStrategy3<CTX, Function<I, String>, Function<O, String>, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final DebugStrategy instance = new DebugStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> DebugStrategy<CTX, I, O> getInstance() { return (DebugStrategy<CTX, I, O>)instance; }

    private DebugStrategy() {}

    @Override
    public String getName() { return "debug"; }

    @Override
    public Seq<O> eval(CTX ctx, Function<I, String> inTransform, Function<O, String> outTransform, Strategy<CTX, I, O> strategy, I input) {
        System.out.println("▶ " + strategy + " ← " + inTransform.apply(input));
        final Seq<O> results = strategy.eval(ctx, input).buffer();

        final List<O> resultsList;
        try {
            resultsList = results.toList().eval();
        } catch (InterruptedException ex) {
            System.out.println("◀︎ " + strategy + " ← " + inTransform.apply(input));
            System.out.println("  INTERRUPTED");
            Thread.currentThread().interrupt();
            return Seq.empty();
        }

        System.out.println("◀︎ " + strategy + " ← " + inTransform.apply(input));
        if(resultsList.isEmpty()) {
            System.out.println("  FAILED");
        } else {
            System.out.println("  " + resultsList.size() + " result" + (resultsList.size() > 1 ? "s:" : ":"));
            for(O result : resultsList) {
                System.out.print("  • ");
                System.out.println(outTransform.apply(result));
            }
        }
        return results;
    }

}
