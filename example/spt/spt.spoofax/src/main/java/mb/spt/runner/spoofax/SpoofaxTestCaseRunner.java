package mb.spt.runner.spoofax;

import mb.common.result.Result;
import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.pie.api.Task;
import mb.spt.ITestCase;
import mb.spt.runner.ITestCaseRunner;
import mb.spt.ITestCaseResult;
import mb.spt.runner.TestCaseRunner;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Spoofax implementation of {@link ITestCaseRunner}.
 */
public final class SpoofaxTestCaseRunner extends TestCaseRunner {

    @Override
    public ITestCaseResult run(ITestCase testCase) throws ExecException, InterruptedException {

        // Each test expectation requires a particular input,
        // such as the parsed fragment, the analyzed fragment, the result of a Stratego strategy, etc.
        // Each of these inputs may have their own dependencies too,
        // such as how an analyzed fragment requires the parsed fragment.

        // TODO: Get the parse task for the LUT
        Task<Result<?, ?>> parseTask = null;
        MixedSession session = null;
        @Nullable final Result<?, ?> result = session.require(parseTask);

        return super.run(testCase);
    }
}
