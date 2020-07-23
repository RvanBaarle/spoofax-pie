package mb.spt;

import mb.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A top-level test suite element that contains a fragment, such as a test case or test fixture.
 */
public interface ITestSuiteElement {

    /**
     * Gets the test suite that this element is part of.
     *
     * @return the parent test suite; or {@code null} when the element is not part of any test suite
     */
    @Nullable ITestSuite getTestSuite();

    /**
     * Gets the fragment of the element.
     *
     * This is the piece of code written in the language under test that is being tested.
     *
     * @return the fragment
     */
    ITestFragment getFragment();

    /**
     * Sets the owner of this element.
     *
     * @param testSuite the test suite that owns this element; or {@code null}
     * @param index the zero-based index of this element; or -1
     */
    void setOwner(ITestSuite testSuite, int index);

}
