package mb.spt;

import mb.common.message.Message;
import mb.common.util.ListView;

public final class TestSuiteResult implements ITestSuiteResult {

    private final ITestSuite testSuite;
    private final boolean isSuccessful;
    private final ListView<Message> messages;
    private final ListView<ITestCaseResult> testCaseResults;

    public TestSuiteResult(ITestSuite testSuite, boolean isSuccessful, ListView<Message> messages, ListView<ITestCaseResult> testCaseResults) {
        this.testSuite = testSuite;
        this.isSuccessful = isSuccessful;
        this.messages = messages;
        this.testCaseResults = testCaseResults;
    }

    @Override
    public ITestSuite getTestSuite() {
        return this.testSuite;
    }

    @Override
    public boolean isSuccessful() {
        return this.isSuccessful;
    }

    @Override
    public ListView<Message> getMessages() {
        return this.messages;
    }

    @Override
    public ListView<ITestCaseResult> getTestCaseResults() {
        return this.testCaseResults;
    }
}
