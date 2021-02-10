package mb.strategies;

import mb.sequences.Sequence;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Repeatedly evaluates the strategy until the set of values no longer changes.
 *
 * @param <CTX> the type of context
 * @param <T> the type of values
 */
public final class FixSetStrategy<CTX, T> implements Strategy1<CTX, Strategy<CTX, T, T>, T, T>{

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
    public Sequence<T> eval(CTX ctx, Strategy<CTX, T, T> s, T input) throws InterruptedException {
        // Use LinkedHashSet to preserve insertion order
        Set<T> newValues = new LinkedHashSet<T>();
        Set<T> values = new LinkedHashSet<T>();
        values.add(input);

        while (true) {
            for(T value : values) {
                final Sequence<T> seq = s.eval(ctx, value);
                for (T t : seq) {
                    newValues.add(t);
                }
            }

            if (newValues.isEmpty()) {
                // Everything failed, we return
                return Sequence.from(values);
            }

            if (values.equals(newValues)) {
                // Everything stayed the same, we return
                return Sequence.from(newValues);
            }

            Set<T> tmp = values;
            values = newValues;
            newValues = tmp;
            newValues.clear();
        }
    }
}
