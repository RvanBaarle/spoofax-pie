package mb.spt;

import mb.resource.Resource;

/**
 * Builder for {@link ITestSuiteElement} objects.
 */
public interface ITestSuiteElementBuilder {

//    /**
//     * Sets the resource of the test suite from which the element was extracted.
//     *
//     * @param resource the resource
//     * @return this builder
//     */
//    ITestSuiteElementBuilder withResource(Resource resource);

    /**
     * Sets the fragment of the element.
     *
     * @param fragment the fragment
     * @return this builder
     */
    ITestSuiteElementBuilder withFragment(ITestFragment fragment);

    /**
     * Builds the test suite element.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test suite element
     */
    ITestSuiteElement build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestSuiteElementBuilder reset();

}
