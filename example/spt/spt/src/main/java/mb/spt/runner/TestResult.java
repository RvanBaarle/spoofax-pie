package mb.spt.runner;

import mb.common.message.Message;
import mb.common.util.ListView;

/**
 * The result of a test element.
 */
public class TestResult {

    private final boolean isSuccessful;
    private final ListView<Message> messages;
    private final ListView<TestResult> subResults;

    public TestResult(boolean isSuccessful, ListView<Message> messages, ListView<TestResult> subResults) {
        this.isSuccessful = isSuccessful;
        this.messages = messages;
        this.subResults = subResults;
    }

    /**
     * Gets whether the test expectation was met.
     *
     * @return {@code true} when the test expectation was met;
     * otherwise, {@code false}
     */
    public boolean isSuccessful() {
        return this.isSuccessful;
    }

    /**
     * Gets any messages returned by evaluating the test expectation.
     *
     * @return a list of messages
     */
    public ListView<Message> getMessages() {
        return this.getMessages();
    }

    public ListView<TestResult> getSubResults() {
        return this.subResults;
    }

}
