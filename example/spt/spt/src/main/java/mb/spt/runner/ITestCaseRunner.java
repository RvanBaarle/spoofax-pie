package mb.spt.runner;

import mb.pie.api.ExecException;
import mb.spt.ITestCase;
import mb.spt.ITestCaseResult;

/**
 * Runs individual test cases.
 */
public interface ITestCaseRunner {

    /**
     * Runs the given test case.
     *
     * @param testCase the test case to run
     * @return the test result
     */
    ITestCaseResult run(ITestCase testCase) throws ExecException, InterruptedException;

}
