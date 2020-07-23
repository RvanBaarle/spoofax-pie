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
public interface ITestExpectationEvaluator<E extends ITestExpectation, I extends ITestInput> {

    /**
     * Evaluates the test expectation.
     *
     * @param testExpectation the test expectation
     * @param input the input
     * @return the result of evaluating the test expectation
     */
    ITestExpectationResult evaluate(E testExpectation, I input) throws ExecException, InterruptedException;

}
