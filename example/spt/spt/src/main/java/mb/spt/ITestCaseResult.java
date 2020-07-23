package mb.spt;

import mb.common.message.Message;
import mb.common.util.ListView;
import mb.spt.expectations.ITestExpectationResult;

import java.util.ArrayList;

/**
 * A test result.
 */
public interface ITestCaseResult {

    /**
     * Gets the corresponding test case for which this is the result.
     *
     * @return the test case
     */
    ITestCase getTestCase();

    // TODO: Test state, such as skipped, timed out, etc
    /**
     * Gets whether the test passed succesfully.
     *
     * @return {@code true} when the test passed;
     * otherwise, {@code false}
     */
    boolean isSuccessful();

    /**
     * Gets any messages returned by evaluating the test case,
     * that where not caused by evaluating test expectations.
     *
     * @return a list of messages
     */
    ListView<Message> getMessages();

    /**
     * Gets all messages returned by evaluating the test case
     * and its test expectations.
     *
     * This includes the messages from {@link #getMessages()} and
     * all messages from each of the {@link #getTestExpectationResults()}.
     *
     * @return a list of messages
     */
    default ListView<Message> getAllMessages() {
        final ArrayList<Message> allMessages = new ArrayList<>();
        getMessages().addAllTo(allMessages);
        getTestExpectationResults().stream().map(r -> r.getMessages()).forEach(m -> m.addAllTo(allMessages));
        return ListView.of(allMessages);
    }

    /**
     * Gets the result of each test expectation in the test case.
     *
     * @return a list of test expectation results
     */
    ListView<ITestExpectationResult> getTestExpectationResults();

}
