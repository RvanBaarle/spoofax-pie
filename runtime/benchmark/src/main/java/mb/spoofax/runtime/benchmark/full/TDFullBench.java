package mb.spoofax.runtime.benchmark.full;

import mb.spoofax.runtime.benchmark.state.InfraState;
import mb.spoofax.runtime.benchmark.state.SpoofaxPieState;
import mb.spoofax.runtime.benchmark.state.WorkspaceState;
import mb.spoofax.runtime.benchmark.state.exec.TDState;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class TDFullBench {
    @Setup(Level.Invocation)
    public void setup(SpoofaxPieState spoofaxPie, WorkspaceState workspace, InfraState infra, TDState exec) throws IOException {
        workspace.setup(spoofaxPie);
        infra.setup(spoofaxPie, workspace);
        exec.setup(spoofaxPie, workspace, infra);

        this.spoofaxPie = spoofaxPie;
        this.workspace = workspace;
        this.infra = infra;
        this.exec = exec;
    }

    private SpoofaxPieState spoofaxPie;
    private WorkspaceState workspace;
    private InfraState infra;
    private TDState exec;

    @Benchmark public void exec(Blackhole blackhole) {
        exec.execAll(blackhole);
    }
}
