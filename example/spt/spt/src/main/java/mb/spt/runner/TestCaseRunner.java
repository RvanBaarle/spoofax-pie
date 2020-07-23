package mb.spt.runner;

import mb.common.util.ListView;
import mb.pie.api.ExecException;
import mb.spt.ITestCase;
import mb.spt.ITestCaseResult;
import mb.spt.ITestExpectationEvaluatorService;
import mb.spt.ITestSuite;
import mb.spt.TestCaseResult;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationResult;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Abstract implementation of {@link ITestCaseRunner}.
 */
public final class TestCaseRunner implements ITestCaseRunner {

    //private final Logger logger;
    private final ITestExpectationEvaluatorService evaluatorService;

    @Inject protected TestCaseRunner(ITestExpectationEvaluatorService evaluatorService) {
        this.evaluatorService = evaluatorService;
    }

    @Override
    public ITestCaseResult run(ITestCase testCase, ITestSuite testSuite) throws ExecException, InterruptedException {
        // Evaluate the expectations
        final ArrayList<ITestExpectationResult> expectationResults = new ArrayList<>();
        for (ITestExpectation expectation : testCase.getExpectations()) {
            final ITestExpectationResult expectationResult = evaluatorService.evaluate(expectation, testCase, testSuite);
            expectationResults.add(expectationResult);
        }

        // Gather the results
        return new TestCaseResult(
            testCase,
            expectationResults.stream().allMatch(r -> r.isSuccessful()),
            ListView.of(),
            ListView.of(expectationResults)
        );
    }

}
