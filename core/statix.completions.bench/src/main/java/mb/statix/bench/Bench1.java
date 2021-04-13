package mb.statix.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class Bench1 {

    @Benchmark
    @Fork(value = 1, warmups = 0)
    public void init() {
        // Do nothing
    }
}
