package mb.spt;

import mb.pie.api.ExecException;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationEvaluator;
import mb.spt.expectations.ITestExpectationResult;

/**
 * Finds an evaluator for a given expectation.
 */
public interface ITestExpectationEvaluatorService {

    /**
     * Find an evaluator that can evaluate the given expectation.
     *
     * @param expectation the expectation for which to find the evaluator
     * @param <E> the type of expectation
     * @return the evaluator
     * @throws IllegalArgumentException no evaluator is known for the specified expectation
     */
    <E extends ITestExpectation> ITestExpectationEvaluator<E> get(E expectation);

    ITestExpectationResult evaluate(ITestExpectation expectation, ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException;

}
