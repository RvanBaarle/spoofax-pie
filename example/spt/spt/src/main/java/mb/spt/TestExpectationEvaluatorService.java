package mb.spt;

import mb.pie.api.ExecException;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationEvaluator;
import mb.spt.expectations.ITestExpectationResult;

public class TestExpectationEvaluatorService implements ITestExpectationEvaluatorService {
    @Override
    public <E extends ITestExpectation> ITestExpectationEvaluator<E> get(E expectation) {
        return null;
    }

    @Override
    public ITestExpectationResult evaluate(ITestExpectation expectation, ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException {
        final ITestExpectationEvaluator<ITestExpectation> evaluator = get(expectation);
        return evaluator.evaluate(expectation, testCase, testSuite);
    }
}
