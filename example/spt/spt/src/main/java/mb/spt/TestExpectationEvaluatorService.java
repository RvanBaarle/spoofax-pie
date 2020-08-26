package mb.spt;

import mb.pie.api.ExecException;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationEvaluator;
import mb.spt.expectations.ITestExpectationResult;

import javax.inject.Inject;

public class TestExpectationEvaluatorService implements ITestExpectationEvaluatorService {

    @Inject public TestExpectationEvaluatorService() {

    }

    @Override public <E extends ITestExpectation> ITestExpectationEvaluator<E> get(E expectation) {
        // TODO
        return null;
    }

    @Override public ITestExpectationResult evaluate(ITestExpectation expectation, ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException {
        final ITestExpectationEvaluator<ITestExpectation> evaluator = get(expectation);
        return evaluator.evaluate(expectation, testCase, testSuite);
    }
}
