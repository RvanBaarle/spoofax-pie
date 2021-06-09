package mb.statix.completions.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;

public class Bench1 {

    @Benchmark
    @Fork(value = 1, warmups = 0)
    public void init() {
        // Do nothing

        // 1. Build all tests (ASTs with a placeholder hole)
        // 2. Complete each test
        // 3.
    }
}
