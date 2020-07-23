package mb.spt.expectations;

import mb.pie.api.ExecException;
import mb.spt.ITestCase;
import mb.spt.ITestInput;
import mb.spt.ITestSuite;

/**
 * A test expectation evaluator evaluates a test expectation.
 *
 * @param <E> the type of test expectation being evaluated
 */
public interface ITestExpectationEvaluator<E extends ITestExpectation> {

    /**
     * Evaluates the test expectation.
     *
     * @param testExpectation the test expectation
     * @param testCase the test case that contains the test expectation
     * @param testSuite the test suite that contains the test case
     * @return the result of evaluating the test expectation
     */
    ITestExpectationResult evaluate(E testExpectation, ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException;

}
