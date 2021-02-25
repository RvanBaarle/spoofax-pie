package mb.strategies;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.sequences.Seq;

public class DebugContext extends StackContext {

    private final Logger log;

    public DebugContext(LoggerFactory loggerFactory) {
        super(loggerFactory);
        this.log = loggerFactory.create(StackContext.class);
    }

    @Override
    public <CTX, T, R> void enter(Strategy<CTX, T, R> strategy, T input) {
        super.enter(strategy, input);
//        printPrefix("▶", stack.size());
//        System.out.println(" " + strategy + " ⟸ " + inTransform.apply(input));
    }

    @Override
    public <CTX, A1, T, R> void enter(Strategy1<CTX, A1, T, R> strategy, A1 arg1, T input) {
        super.enter(strategy, arg1, input);
    }

    @Override
    public <CTX, A1, A2, T, R> void enter(Strategy2<CTX, A1, A2, T, R> strategy, A1 arg1, A2 arg2, T input) {
        super.enter(strategy, arg1, arg2, input);
    }

    @Override
    public <CTX, A1, A2, A3, T, R> void enter(Strategy3<CTX, A1, A2, A3, T, R> strategy, A1 arg1, A2 arg2, A3 arg3, T input) {
        super.enter(strategy, arg1, arg2, arg3, input);
    }

    @Override
    public void exit(StrategyDecl strategy, Seq<?> results) {
        super.exit(strategy, results);
    }

    private static void printPrefix(String s, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(s);
        }
    }
}
