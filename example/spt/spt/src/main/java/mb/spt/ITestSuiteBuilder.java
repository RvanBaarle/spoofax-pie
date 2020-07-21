package mb.spt;

/**
 * Builder for {@link ITestSuite} objects.
 */
public interface ITestSuiteBuilder {

    /**
     * Sets the name of the test suite.
     *
     * @param name the name of the test suite
     * @return this builder
     */
    ITestSuiteBuilder withName(String name);

    /**
     * Adds a test element to the test suite.
     *
     * The new element is added to the end of the list of elements.
     *
     * @param element the test element to add.
     * @return this builder
     */
    ITestSuiteBuilder addTestElement(ITestSuiteElement element);

    /**
     * Builds the test suite.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test suite
     */
    ITestSuite build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestSuiteBuilder reset();

}
