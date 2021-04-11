package mb.statix.bench;

import org.openjdk.jmh.annotations.Benchmark;

/** Main class. */
public final class Main {
    private Main() { /* Cannot be instantiated. */ }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
