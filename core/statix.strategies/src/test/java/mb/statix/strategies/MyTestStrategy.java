package mb.statix.strategies;

import mb.statix.lazy.LazySeq;
import mb.statix.sequences.Computation;

import java.util.concurrent.atomic.AtomicInteger;

public class MyTestStrategy extends NamedStrategy<Object, String, String> {
    public final AtomicInteger doEvalCalls = new AtomicInteger();

    @Override
    public LazySeq<String> eval(Object ctx, String input) {
        doEvalCalls.incrementAndGet();
        return LazySeq.fromOnce(() -> input);
    }

    @Override
    public String getName() {
        return "my-test-strategy";
    }

    @Override
    public String getParamName(int index) {
        throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");
    }

    @Override
    public int getPrecedence() {
        return 42;
    }

    @Override
    public void writeArg(StringBuilder sb, int index, Object arg) {
        sb.append(index).append(": ");    // Prefix added for testing
        super.writeArg(sb, index, arg);
    }
}
