package mb.spt;

import mb.common.util.ListView;

/**
 * Implementation of {@link ITestSuite}.
 */
public class TestSuite implements ITestSuite {

    private final String name;
    private final ListView<ITestSuiteElement> testElements;

    protected TestSuite(String name, ListView<ITestSuiteElement> testElements) {
        this.name = name;
        this.testElements = testElements;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public ListView<ITestSuiteElement> getTestElements() {
        return this.testElements;
    }
}
