package mb.spt.expectations;

import mb.common.message.Message;
import mb.common.util.ListView;

/**
 * The result of a test expectation on a test case.
 */
public interface ITestExpectationResult {

    /**
     * Gets the corresponding test expectation for which this is the result.
     *
     * @return the test expectation
     */
    ITestExpectation getTestExpectation();

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

}
