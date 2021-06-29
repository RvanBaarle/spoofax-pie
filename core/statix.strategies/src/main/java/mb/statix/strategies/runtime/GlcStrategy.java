package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.InterruptibleIteratorBase;
import mb.statix.sequences.Seq;
import mb.statix.strategies.NamedStrategy3;
import mb.statix.strategies.Strategy;

/**
 * Guarded left choice strategy.
 *
 * @param <CTX> the type of context (invariant)
 * @param <T> the type of input (contravariant)
 * @param <U> the type of intermediate (invariant)
 * @param <R> the type of output (covariant)
 */
public final class GlcStrategy<CTX, T, U, R> extends NamedStrategy3<CTX, Strategy<CTX, T, U>, Strategy<CTX, U, R>, Strategy<CTX, T, R>, T, R> {

    @SuppressWarnings("rawtypes")
    private static final GlcStrategy instance = new GlcStrategy();
    @SuppressWarnings("unchecked")
    public static <CTX, T, U, R> GlcStrategy<CTX, T, U, R> getInstance() { return (GlcStrategy<CTX, T, U, R>)instance; }

    private GlcStrategy() { /* Prevent instantiation. Use getInstance(). */ }

    @Override
    public Seq<R> eval(CTX ctx, Strategy<CTX, T, U> condition, Strategy<CTX, U, R> onSuccess, Strategy<CTX, T, R> onFailure, T input) {
        final Seq<U> conditionSeq = condition.eval(ctx, input);
        return () -> new InterruptibleIteratorBase<R>() {
            // Iterable from the sequence whose results we're returning
            final InterruptibleIterator<U> conditionIter = conditionSeq.iterator();

            // Implementation if `yield` and `yieldBreak` could actually suspend computation
            @SuppressWarnings("unused")
            private void computeNextCoroutine() throws InterruptedException {
                // 0:
                if(conditionIter.hasNext()) {
                    // 1:
                    while(conditionIter.hasNext()) {
                        final U next = conditionIter.next();
                        final Seq<R> onSuccessSeq = onSuccess.eval(ctx, next);
                        final InterruptibleIterator<R> onSuccessIter = onSuccessSeq.iterator();
                        // 2:
                        while(onSuccessIter.hasNext()) {
                            this.yield(onSuccessIter.next());
                            // 3:
                        }
                        // 4:
                    }
                    // 5:
                } else {
                    // 6:
                    final InterruptibleIterator<R> onFailureIter = onFailure.eval(ctx, input).iterator();
                    // 7:
                    while(onFailureIter.hasNext()) {
                        this.yield(onFailureIter.next());
                        // 8:
                    }
                    // 9:
                }
                // 10:
                yieldBreak();
            }

            // STATE MACHINE
            private int state = 0;
            // LOCAL VARIABLES
            private InterruptibleIterator<R> onSuccessIter;
            private InterruptibleIterator<R> onFailureIter;

            @Override
            protected void computeNext() throws InterruptedException {
                while (true) {
                    switch (state) {
                        case 0:
                            if (!conditionIter.hasNext()) {
                                this.state = 6;
                                continue;
                            }
                            this.state = 1;
                            continue;
                        case 1:
                            if (!conditionIter.hasNext()) {
                                this.state = 5;
                                continue;
                            }
                            final U next = conditionIter.next();
                            final Seq<R> onSuccessSeq = onSuccess.eval(ctx, next);
                            onSuccessIter = onSuccessSeq.iterator();
                            this.state = 2;
                            continue;
                        case 2:
                            if (!onSuccessIter.hasNext()) {
                                this.state = 4;
                                continue;
                            }
                            yield(onSuccessIter.next());
                            this.state = 3;
                            return;
                        case 3:
                            this.state = 2;
                            continue;
                        case 4:
                            this.state = 1;
                            continue;
                        case 5:
                            this.state = 10;
                            continue;
                        case 6:
                            onFailureIter = onFailure.eval(ctx, input).iterator();
                            this.state = 7;
                            continue;
                        case 7:
                            if (!onFailureIter.hasNext()) {
                                this.state = 9;
                                continue;
                            }
                            yield(onFailureIter.next());
                            this.state = 8;
                            return;
                        case 8:
                            this.state = 7;
                            continue;
                        case 9:
                            this.state = 10;
                            continue;
                        case 10:
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
        return "glc";
    }

    @Override
    public String getParamName(int index) {
        switch (index) {
            case 0: return "condition";
            case 1: return "onSuccess";
            case 2: return "onFailure";
            default: return super.getParamName(index);
        }
    }
}
