package mb.spt;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Builder for {@link ITestFixture} objects.
 */
public interface ITestFixtureBuilder extends ITestSuiteElementBuilder {

    /**
     * Sets the container of the test fixture.
     *
     * @param container the container
     * @return this builder
     */
    ITestFixtureBuilder withContainer(OwnedList<? super ITestFixture, ITestSuite> container);

    /**
     * Builds the test fixture.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test fixture
     */
    ITestFixture build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestFixtureBuilder reset();

}
