package mb.spt;

import mb.common.message.Message;
import mb.common.util.ListView;
import mb.spt.expectations.ITestExpectationResult;

import java.util.Objects;

/**
 * A test case result.
 */
public final class TestCaseResult implements ITestCaseResult {

    private final ITestCase testCase;
    private final boolean isSuccessful;
    private final ListView<Message> messages;
    private final ListView<ITestExpectationResult> expectationResults;

    public TestCaseResult(
        ITestCase testCase,
        boolean isSuccessful,
        ListView<Message> messages,
        ListView<ITestExpectationResult> expectationResults
    ) {
        this.testCase = testCase;
        this.isSuccessful = isSuccessful;
        this.messages = messages;
        this.expectationResults = expectationResults;
    }

    @Override public ITestCase getTestCase() {
        return testCase;
    }

    @Override public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override public ListView<Message> getMessages() {
        return messages;
    }

    @Override public ListView<ITestExpectationResult> getTestExpectationResults() {
        return expectationResults;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestCaseResult that = (TestCaseResult)o;
        return isSuccessful == that.isSuccessful
            && messages.equals(that.messages)
            && expectationResults.equals(that.expectationResults);
    }

    @Override public int hashCode() {
        return Objects.hash(isSuccessful, messages, expectationResults);
    }

    @Override public String toString() {
        return "TestCaseResult{" +
            "isSuccessful=" + isSuccessful + ", " +
            "messages=" + messages + ", " +
            "expectationResults=" + expectationResults +
            '}';
    }
}
