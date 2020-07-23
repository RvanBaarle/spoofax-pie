package mb.spt.expectations.spoofax;

import mb.spt.ISpoofaxTestCodeInput;
import mb.spt.ITestCase;
import mb.spt.ITestSuite;
import mb.spt.inputs.ITestCodeInputProvider;

public interface ISpoofaxTestCodeInputProvider extends ITestCodeInputProvider {

    @Override ISpoofaxTestCodeInput get(ITestCase testCase, ITestSuite testSuite);

}
