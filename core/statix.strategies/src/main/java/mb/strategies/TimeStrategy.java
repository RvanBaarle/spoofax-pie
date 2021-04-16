package mb.strategies;

import mb.sequences.InterruptibleIterator;
import mb.sequences.Seq;

import java.util.List;
import java.util.function.Function;
/**
 * Evaluates a strategy and prints both its entrance and its results.
 *
 * @param <CTX> the type of context
 * @param <I> the type of input
 * @param <O> the type of outputs
 */
public final class TimeStrategy<CTX, I, O> extends AbstractStrategy2<CTX, String, Strategy<CTX, I, O>, I, O>{

    @SuppressWarnings("rawtypes")
    private static final TimeStrategy instance = new TimeStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, I, O> TimeStrategy<CTX, I, O> getInstance() { return (TimeStrategy<CTX, I, O>)instance; }

    private TimeStrategy() {}

    @Override
    public String getName() { return "time"; }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "name";
            case 1: return "strategy";
            default: return super.getParamName(index);
        }
    }

    public static boolean debug = true;
    private static int level = 1;

    @Override
    protected Seq<O> innerEval(CTX ctx, String name, Strategy<CTX, I, O> strategy, I input) {
        if (debug) {
            printLevelPrefix("▶");
            System.out.println(" " + name);
        }
        level += 1;
        long startTime = System.nanoTime();

        Seq<O> results = strategy.eval(ctx, input).buffer();
        try {
            results.any(v -> false).eval();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return Seq.empty();
        }
        long endTime = System.nanoTime();
        long diffInMs = (endTime - startTime) / 1000000;

        //System.out.println("TIMED: " + name + ": " + diffInMs + "ms");
        level -= 1;
        if (debug) {
            printLevelPrefix("◀");
            System.out.println(" " + name + " ("+ diffInMs +"ms)");
        }
        return results;
    }

    private static void printLevelPrefix(String s) {
        for (int i = 0; i < level; i++) {
            System.out.print(s);
        }
    }


}
