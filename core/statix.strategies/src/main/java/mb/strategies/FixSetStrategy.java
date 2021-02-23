package mb.strategies;

import mb.sequences.ComputingInterruptibleIterator;
import mb.sequences.Seq;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Repeatedly evaluates the strategy until the set of values no longer changes.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class FixSetStrategy<CTX, T> extends AbstractStrategy1<CTX, Strategy<CTX, T, T>, T, T>{

    @SuppressWarnings("rawtypes")
    private static final FixSetStrategy instance = new FixSetStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> FixSetStrategy<CTX, T> getInstance() { return (FixSetStrategy<CTX, T>)instance; }

    private FixSetStrategy() {}

    @Override
    public String getName() {
        return "fixSet";
    }

    @Override
    public Seq<T> eval(CTX ctx, Strategy<CTX, T, T> s, T input) {
        return () -> new ComputingInterruptibleIterator<T>() {
            // TODO: Can we optimize this to not compute all values in advance?
            @Override
            protected Iterable<T> computeAll() throws InterruptedException {
                // Use LinkedHashSet to preserve insertion order
                Set<T> newValues = new LinkedHashSet<T>();
                Set<T> values = new LinkedHashSet<T>();
                values.add(input);
                while (true) {
                    for(T value : values) {
                        final Seq<T> seq = s.eval(ctx, value);
                        seq.iterator().forEachRemaining(newValues::add);
                    }

                    if (newValues.isEmpty()) {
                        // Everything failed, we return
                        return values;
                    }

                    if (values.equals(newValues)) {
                        // Everything stayed the same, we return
                        return newValues;
                    }

                    Set<T> tmp = values;
                    values = newValues;
                    newValues = tmp;
                    newValues.clear();
                }
            }
        };
    }

}
