package mb.spt;

import mb.common.message.Message;
import mb.common.util.ListView;

/**
 * A test suite result.
 */
public interface ITestSuiteResult {

    /**
     * Gets the corresponding test suite for which this is the result.
     *
     * @return the test suite
     */
    ITestSuite getTestSuite();

    /**
     * Gets whether the test suite is successful.
     *
     * (Iff there were no errors during parsing and analysis.)
     *
     * @return {@code true} when the test suite is successful;
     * otherwise, {@code false}
     */
    boolean isSuccessful();

    /**
     * Gets any messages returned by evaluating the test suite,
     * that where not caused by evaluating test cases.
     *
     * @return a list of messages
     */
    ListView<Message> getMessages();

    /**
     * Gets all messages returned by evaluating the test suite
     * and its test cases.
     *
     * This includes the messages from {@link #getMessages()} and
     * all messages from each of the {@link #getTestExpectationResults()}.
     *
     * @return a list of messages
     */
    ListView<Message> getAllMessages();

    /**
     * Gets the result of each test case in the test suite.
     *
     * @return a list of test cases results
     */
    ListView<ITestCaseResult> getTestCaseResults();

}
