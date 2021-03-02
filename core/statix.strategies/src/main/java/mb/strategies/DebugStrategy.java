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

    public static boolean debug = false;
    private static int level = 1;

    @Override
    public Seq<O> eval(CTX ctx, Function<I, String> inTransform, Function<O, String> outTransform, Strategy<CTX, I, O> strategy, I input) {
        if (debug) {
            printLevelPrefix("▶");
            System.out.println(" " + strategy + " ⟸ " + inTransform.apply(input));
        }
        level += 1;
        // TODO: Investigate why removing buffer() here is negative for performance!
        Seq<O> results = strategy.eval(ctx, input).buffer();
//        if (debug) {
//            results = results.buffer();
//        }

        final List<O> resultsList;
        try {
            resultsList = results.toList().eval();
        } catch (InterruptedException ex) {
            level -= 1;
            if (debug) {
                printLevelPrefix("◀");
                System.out.println(" " + strategy + " ⟸ " + inTransform.apply(input));
                printLevelPrefix(" ");
                System.out.println(" INTERRUPTED");
            }
            Thread.currentThread().interrupt();
            return Seq.empty();
        }


        level -= 1;
        if (debug) {
            printLevelPrefix("◀");
            System.out.println(" " + strategy + " ⟸ " + inTransform.apply(input));
            if(resultsList.isEmpty()) {
                printLevelPrefix(" ");
                System.out.println(" FAILED");
            } else {
                printLevelPrefix(" ");
                System.out.println(" " + resultsList.size() + " result" + (resultsList.size() > 1 ? "s:" : ":"));
                for(O result : resultsList) {
                    printLevelPrefix(" ");
                    System.out.print(" • ");
                    System.out.println(outTransform.apply(result));
                }
            }
        }
        return results;
    }

    private static void printLevelPrefix(String s) {
        for (int i = 0; i < level; i++) {
            System.out.print(s);
        }
    }

}
