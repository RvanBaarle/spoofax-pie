package mb.strategies;

import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.sequences.Seq;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class StackContext implements Context{
    protected final Deque<StackFrame> stack = new LinkedList<>();
    private final Logger log;

    public StackContext(LoggerFactory loggerFactory) {
        this.log = loggerFactory.create(StackContext.class);
    }

    @Override
    public <CTX, T, R> void enter(Strategy<CTX, T, R> strategy, T input) {
        stack.push(new StackFrame(strategy, Collections.emptyList(), input));
    }

    @Override
    public <CTX, A1, T, R> void enter(Strategy1<CTX, A1, T, R> strategy, A1 arg1, T input) {
        stack.push(new StackFrame(strategy, Collections.singletonList(arg1), input));
    }

    @Override
    public <CTX, A1, A2, T, R> void enter(Strategy2<CTX, A1, A2, T, R> strategy, A1 arg1, A2 arg2, T input) {
        stack.push(new StackFrame(strategy, Arrays.asList(arg1, arg2), input));
    }

    @Override
    public <CTX, A1, A2, A3, T, R> void enter(Strategy3<CTX, A1, A2, A3, T, R> strategy, A1 arg1, A2 arg2, A3 arg3, T input) {
        stack.push(new StackFrame(strategy, Arrays.asList(arg1, arg2, arg3), input));
    }

    @Override
    public void exit(StrategyDecl strategy, Seq<?> results) {
        final StackFrame frame = stack.pop();
        if (frame.strategy != strategy) {
            log.warn("Stack corrupted. Expected exit of: " + frame.strategy + ", got exit of: " + strategy);
        }
    }

    private static final class StackFrame {
        private final StrategyDecl strategy;
        private final List<Object> args;
        private final Object input;


        private StackFrame(StrategyDecl strategy, List<Object> args, Object input) {
            this.strategy = strategy;
            this.args = args;
            this.input = input;
        }
    }
}
