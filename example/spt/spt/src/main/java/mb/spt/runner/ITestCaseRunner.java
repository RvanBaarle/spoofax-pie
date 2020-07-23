package mb.spt.runner;

import mb.pie.api.ExecException;
import mb.spt.ITestCase;
import mb.spt.ITestCaseResult;
import mb.spt.ITestSuite;

/**
 * Runs individual test cases.
 */
public interface ITestCaseRunner {

    /**
     * Runs the given test case.
     *
     * @param testCase the test case to run
     * @param testSuite the test suite that contains the test case
     * @return the test result
     */
    ITestCaseResult run(ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException;

}
