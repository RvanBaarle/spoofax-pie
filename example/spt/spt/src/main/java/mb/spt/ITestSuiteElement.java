package mb.spt;

import mb.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A top-level test suite element that contains a fragment, such as a test case or test fixture.
 */
public interface ITestSuiteElement {

    /**
     * Gets the fragment of the element.
     *
     * This is the piece of code written in the language under test that is being tested.
     *
     * @return the fragment
     */
    ITestFragment getFragment();

}
