package mb.spt.runner;

import mb.pie.api.ExecException;
import mb.spt.ITestCase;
import mb.spt.ITestCaseResult;

/**
 * Abstract implementation of {@link ITestCaseRunner}.
 */
public abstract class TestCaseRunner implements ITestCaseRunner {

    //private final Logger logger;

    @Override
    public ITestCaseResult run(ITestCase testCase) throws ExecException, InterruptedException {

        // Each test expectation requires a particular input,
        // such as the parsed fragment, the analyzed fragment, the result of a Stratego strategy, etc.
        // Each of these inputs may have their own dependencies too,
        // such as how an analyzed fragment requires the parsed fragment.


        return null;
    }

}
