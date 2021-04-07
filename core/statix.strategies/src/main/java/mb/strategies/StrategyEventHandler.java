package mb.strategies;

import mb.sequences.Seq;

import java.io.Closeable;
import java.io.IOException;

/**
 * Handles strategy events.
 */
public interface StrategyEventHandler extends Closeable {

    <CTX, I, O> I enter(Strategy<CTX, I, O> strategy, CTX ctx, I input);
    <CTX, A1, I, O> I enter(Strategy1<CTX, A1, I, O> strategy, CTX ctx, A1 arg1, I input);
    <CTX, A1, A2, I, O> I enter(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, I input);
    <CTX, A1, A2, A3, I, O> I enter(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input);

    <CTX, I, O> Seq<O> leave(Strategy<CTX, I, O> strategy, CTX ctx, Seq<O> output);
    <CTX, A1, I, O> Seq<O> leave(Strategy1<CTX, A1, I, O> strategy, CTX ctx, Seq<O> output);
    <CTX, A1, A2, I, O> Seq<O> leave(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, Seq<O> output);
    <CTX, A1, A2, A3, I, O> Seq<O> leave(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, Seq<O> output);

    /**
     * Gets the no-op implementation.
     * @return The no-op implementation.
     */
    static StrategyEventHandler none() { return NullStrategyEventHandler.instance; }
}
