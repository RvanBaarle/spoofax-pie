package mb.spt.expectations;

import mb.common.message.Message;
import mb.spt.ITestFragment;
import mb.spt.ITestFragmentBuilder;

/**
 * Builder for {@link ITestExpectationResult} instances.
 */
public interface ITestExpectationResultBuilder {

    /**
     * Sets the corresponding test expectation.
     *
     * @param testExpectation the test expectation
     * @return this builder
     */
    ITestExpectationResultBuilder withTestExpectation(ITestExpectation testExpectation);

    /**
     * Sets whether the test expectation was met.
     *
     * @param isSuccessful {@code true} when the test expectation was met;
     * otherwise, {@code false}
     * @return this builder
     */
    ITestExpectationResultBuilder isSuccessful(boolean isSuccessful);

    /**
     * Adds a message to the test expectation result.
     *
     * @param message the message to add
     * @return this builder
     */
    ITestExpectationResultBuilder addMessage(Message message);

    /**
     * Builds the test expectation result.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test expectation result
     */
    ITestExpectationResult build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestExpectationResultBuilder reset();
}
