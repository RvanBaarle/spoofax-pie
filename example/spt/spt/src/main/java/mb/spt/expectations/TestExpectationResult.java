package mb.spt.expectations;

import mb.common.message.Message;
import mb.common.util.ListView;

/**
 * Implementation of {@link ITestExpectationResult}.
 */
public final class TestExpectationResult implements ITestExpectationResult {

    private final ITestExpectation testExpectation;
    private final boolean isSuccessful;
    private final ListView<Message> messages;

    public TestExpectationResult(
        ITestExpectation testExpectation, boolean isSuccessful, ListView<Message> messages
    ) {
        this.testExpectation = testExpectation;
        this.isSuccessful = isSuccessful;
        this.messages = messages;
    }

    @Override
    public ITestExpectation getTestExpectation() {
        return testExpectation;
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public ListView<Message> getMessages() {
        return messages;
    }
}
