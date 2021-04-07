package mb.strategies;

import mb.sequences.Seq;

import java.io.IOException;

/**
 * Null implementation.
 */
/* package private */ final class NullStrategyEventHandler implements StrategyEventHandler {

    public static final NullStrategyEventHandler instance = new NullStrategyEventHandler();

    private NullStrategyEventHandler() {}

    @Override
    public <CTX, I, O> I enter(Strategy<CTX, I, O> strategy, CTX ctx, I input) {
        // Nothing to do.
        return input;
    }

    @Override
    public <CTX, A1, I, O> I enter(Strategy1<CTX, A1, I, O> strategy, CTX ctx, A1 arg1, I input) {
        // Nothing to do.
        return input;
    }

    @Override
    public <CTX, A1, A2, I, O> I enter(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, I input) {
        // Nothing to do.
        return input;
    }

    @Override
    public <CTX, A1, A2, A3, I, O> I enter(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, A1 arg1, A2 arg2, A3 arg3, I input) {
        // Nothing to do.
        return input;
    }

    @Override
    public <CTX, I, O> Seq<O> leave(Strategy<CTX, I, O> strategy, CTX ctx, Seq<O> output) {
        // Nothing to do.
        return output;
    }

    @Override
    public <CTX, A1, I, O> Seq<O> leave(Strategy1<CTX, A1, I, O> strategy, CTX ctx, Seq<O> output) {
        // Nothing to do.
        return output;
    }

    @Override
    public <CTX, A1, A2, I, O> Seq<O> leave(Strategy2<CTX, A1, A2, I, O> strategy, CTX ctx, Seq<O> output) {
        // Nothing to do.
        return output;
    }

    @Override
    public <CTX, A1, A2, A3, I, O> Seq<O> leave(Strategy3<CTX, A1, A2, A3, I, O> strategy, CTX ctx, Seq<O> output) {
        // Nothing to do.
        return output;
    }

    @Override
    public void close() throws IOException {
        // Nothing to do.
    }
}
