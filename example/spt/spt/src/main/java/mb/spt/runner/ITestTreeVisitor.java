package mb.spt.runner;

import mb.spt.ITestCase;
import mb.spt.ITestFixture;
import mb.spt.ITestSuite;
import mb.spt.expectations.ITestExpectation;

public interface ITestTreeVisitor<R> {

    R visitTestSuite(ITestSuite testSuite);
    R visitTestCase(ITestCase testCase);
    R visitTestExpectation(ITestExpectation testExpectation);

}
