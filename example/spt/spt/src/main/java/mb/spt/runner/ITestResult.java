package mb.spt.runner;

import mb.common.message.Message;
import mb.common.util.ListView;
import mb.spt.expectations.ITestExpectation;

/**
 * The result of a test element.
 */
public interface ITestResult {

    /**
     * Gets whether the test expectation was met.
     *
     * @return {@code true} when the test expectation was met;
     * otherwise, {@code false}
     */
    boolean isSuccessful();

    /**
     * Gets any messages returned by evaluating the test expectation.
     *
     * @return a list of messages
     */
    ListView<Message> getMessages();

    ListView<ITestResult> getSubResults();

}
