package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.InterruptibleIteratorBase;
import mb.statix.sequences.Seq;
import mb.statix.strategies.NamedStrategy1;
import mb.statix.strategies.Strategy;

import java.util.ArrayDeque;
import java.util.HashSet;

/**
 * Fix-set strategy.
 *
 * This repeats applying the strategy, until the strategy fails or the resulting set no longer changes.
 *
 * Implementation: note that we don't have to compute the whole set in advance. Given a value X,
 * if {@code <s> X} fails, X is returned. Otherwise, if {@code <s> X} returns X among its results,
 * it is returned. In both cases, the strategy is no longer applied to any future X.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input and output (invariant)
 */
public final class FixSetStrategy<CTX, T> extends NamedStrategy1<CTX, Strategy<CTX, T, T>, T, T> {

    @SuppressWarnings("rawtypes")
    private static final FixSetStrategy instance = new FixSetStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T> FixSetStrategy<CTX, T> getInstance() { return (FixSetStrategy<CTX, T>)instance; }

    private FixSetStrategy() { /* Prevent instantiation. Use getInstance(). */ }

    @Override
    public Seq<T> eval(CTX ctx, Strategy<CTX, T, T> s, T input) {
        return () -> new InterruptibleIteratorBase<T>() {
            // Implementation if `yield` and `yieldBreak` could actually suspend computation
            @SuppressWarnings("unused")
            private void computeNextCoroutine() throws InterruptedException {
                // To avoid as many computations as possible,
                // this implementation maintains a stack of iterators.
                // Each time the strategy is evaluated, the resulting iterator
                // is pushed on the stack. As long as the iterator is not iterated,
                // no computations will be done.
                // 0:
                final HashSet<T> visited = new HashSet<>();
                final HashSet<T> yielded = new HashSet<>();
                final ArrayDeque<InterruptibleIterator<T>> stack = new ArrayDeque<>();
                stack.push(InterruptibleIterator.of(input));
                // 1:
                while (!stack.isEmpty()) {
                    // 2:
                    // Get the next non-empty iterator on the stack
                    final InterruptibleIterator<T> iterator = stack.peek();
                    if (!iterator.hasNext()) {
                        stack.pop();
                        continue;
                    }
                    final T element = iterator.next();
                    if (!visited.contains(element)) {
                        // We have not previously handled this element,
                        // so we mark it visited and not handle it again
                        visited.add(element);
                        final InterruptibleIterator<T> result = s.eval(ctx, element).iterator();
                        if(!result.hasNext()) {
                            // The strategy failed. Yield the element itself.
                            yielded.add(element);
                            this.yield(element);
                            // 3:
                        } else {
                            // The strategy succeeded. Push the resulting iterator on the stack.
                            stack.push(result);
                        }
                    } else if (!yielded.contains(element)){
                        // We have previously handled this element
                        // so we're going to yield it and not yield it again
                        yielded.add(element);
                        this.yield(element);
                        // 5:
                    }
                    // 6:
                }
                // 7:
                yieldBreak();
            }

            // STATE MACHINE
            private int state = 0;
            // LOCAL VARIABLES
            private final HashSet<T> visited = new HashSet<>();
            private final HashSet<T> yielded = new HashSet<>();
            private final ArrayDeque<InterruptibleIterator<T>> stack = new ArrayDeque<>();

            @Override
            protected void computeNext() throws InterruptedException {
                while (true) {
                    switch (state) {
                        case 0:
                            stack.push(InterruptibleIterator.of(input));
                            this.state = 1;
                            continue;
                        case 1:
                            if (stack.isEmpty()) {
                                this.state = 7;
                                continue;
                            }
                            this.state = 2;
                            continue;
                        case 2:
                            // Get the next non-empty iterator on the stack
                            final InterruptibleIterator<T> iterator = stack.peek();
                            if (!iterator.hasNext()) {
                                stack.pop();
                                this.state = 1;
                                continue;
                            }
                            final T element = iterator.next();
                            if (visited.contains(element)) {
                                if (!yielded.contains(element)){
                                    // We have previously handled this element
                                    // so we're going to yield it and not yield it again
                                    yielded.add(element);
                                    this.yield(element);
                                    this.state = 5;
                                    return;
                                }
                                this.state = 6;
                                continue;
                            }
                            // We have not previously handled this element,
                            // so we mark it visited and not handle it again
                            visited.add(element);
                            final InterruptibleIterator<T> result = s.eval(ctx, element).iterator();
                            if (!result.hasNext()) {
                                // The strategy failed. Yield the element itself.
                                yielded.add(element);
                                this.yield(element);
                                this.state = 3;
                                return;
                            }
                            // The strategy succeeded. Push the resulting iterator on the stack.
                            stack.push(result);
                            this.state = 6;
                            continue;
                        case 3:
                            this.state = 6;
                            continue;
                        case 5:
                            this.state = 6;
                            continue;
                        case 6:
                            this.state = 1;
                            continue;
                        case 7:
                            yieldBreak();
                            this.state = -1;
                            return;
                        default:
                            throw new IllegalStateException("Illegal state: " + state);
                    }
                }
            }
        };
    }

    @Override
    public String getName() {
        return "fixSet";
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "s";
            default: return super.getParamName(index);
        }
    }
}
