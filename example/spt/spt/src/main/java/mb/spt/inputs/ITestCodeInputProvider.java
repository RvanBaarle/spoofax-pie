package mb.spt.inputs;

import mb.spt.ITestCase;
import mb.spt.ITestCodeInput;
import mb.spt.ITestSuite;

public interface ITestCodeInputProvider {

    ITestCodeInput get(ITestCase testCase, ITestSuite testSuite);

}
