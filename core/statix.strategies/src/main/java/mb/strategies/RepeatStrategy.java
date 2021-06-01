package mb.strategies;

import mb.sequences.ComputingInterruptibleIterator;
import mb.sequences.Seq;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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

//    @Override
//    protected Seq<T> innerEval(CTX ctx, Strategy<CTX, T, T> s, T input) {
//        // = try(s ; repeat(s))
//        // = rec x : try(s ; x)
//        return Strategies.<CTX, T, T>rec(x -> try_(seq(s).$(x).$())).eval(ctx, input);
//    }

    @Override
    protected Seq<T> innerEval(CTX ctx, Strategy<CTX, T, T> s, T input) {
        return () -> new ComputingInterruptibleIterator<T>() {
            // TODO: Can we optimize this to not compute all values in advance?
            @Override
            protected Iterable<T> computeAll() throws InterruptedException {
                ArrayList<T> results = new ArrayList<T>();
                ArrayList<T> newValues = new ArrayList<T>();
                ArrayList<T> values = new ArrayList<T>();
                values.add(input);
                while (!values.isEmpty()) {
                    for(T value : values) {
                        final Seq<T> seq = s.eval(ctx, value);
                        if (seq.none().eval()) {
                            // The strategy failed
                            results.add(value);
                        } else {
                            // The strategy succeeded
                            seq.iterator().forEachRemaining(newValues::add);
                        }
                    }

                    ArrayList<T> tmp = values;
                    values = newValues;
                    newValues = tmp;
                    newValues.clear();
                }
                return results;
            }
        };
    }

    @Override
    public String getName() {
        return "repeat";
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches") @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s";
            default: return super.getParamName(index);
        }
    }
}
