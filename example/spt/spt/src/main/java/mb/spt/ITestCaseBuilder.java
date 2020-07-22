package mb.spt;

import mb.common.region.Region;
import mb.spt.expectations.ITestExpectation;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Builder for {@link ITestCase} objects.
 */
public interface ITestCaseBuilder extends ITestSuiteElementBuilder {

    /**
     * Sets the description or name of the test case.
     *
     * @param description the description
     * @return this builder
     */
    ITestCaseBuilder withDescription(String description);

    /**
     * Sets the source region covered by the test's description.
     *
     * @param region the description region; or {@code null} when not known
     * @return this builder
     */
    ITestCaseBuilder withDescriptionRegion(@Nullable Region region);

    /**
     * Adds a test expectation to the test suite.
     *
     * @param testExpectation the test expectation to add.
     * @return this builder
     */
    ITestCaseBuilder addTestExpectation(ITestExpectation testExpectation);

    @Override ITestCaseBuilder withFragment(ITestFragment fragment);

    /**
     * Builds the test case.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test case
     */
    ITestCase build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestCaseBuilder reset();

}
