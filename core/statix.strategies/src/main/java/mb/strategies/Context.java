package mb.strategies;

import mb.sequences.Seq;

public interface Context {

    <CTX, T, R> void enter(Strategy<CTX, T, R> strategy, T input);
    <CTX, A1, T, R> void enter(Strategy1<CTX, A1, T, R> strategy, A1 arg1, T input);
    <CTX, A1, A2, T, R> void enter(Strategy2<CTX, A1, A2, T, R> strategy, A1 arg1, A2 arg2, T input);
    <CTX, A1, A2, A3, T, R> void enter(Strategy3<CTX, A1, A2, A3, T, R> strategy, A1 arg1, A2 arg2, A3 arg3, T input);

    void exit(StrategyDecl strategy, Seq<?> results);

}
