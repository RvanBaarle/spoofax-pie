package mb.spt.expectations;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Builder for {@link ITestExpectation} objects.
 */
public interface ITestExpectationBuilder {

    /**
     * Sets the syntax region of the test expectation.
     *
     * @param region the test expectation syntax region; or {@code null} when it is not known
     * @return this builder
     */
    ITestExpectationBuilder withRegion(@Nullable Region region);

    /**
     * Builds the test expectation.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test case
     */
    ITestExpectation build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestExpectationBuilder reset();

}
