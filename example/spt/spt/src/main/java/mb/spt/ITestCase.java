package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.spt.expectations.ITestExpectation;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A test case.
 */
public interface ITestCase extends ITestSuiteElement {

    /**
     * Gets the description or name of the test case.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets the source region covered by the test's description.
     *
     * This is used for error reporting on the test,
     * when there is no corresponding region in the test fragment.
     *
     * @return the description region; or {@code null} when not known
     */
    @Nullable Region getDescriptionRegion();

    /**
     * Gets the fragment of the test case.
     *
     * This is the piece of code written in the language under test that is being tested.
     *
     * @return the fragment
     */
    ITestFragment getFragment();

    /**
     * Gets the test expectations for this test case.
     *
     * @return a list of test expectations
     */
    ListView<ITestExpectation> getExpectations();

}
