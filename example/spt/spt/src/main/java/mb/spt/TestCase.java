package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;
import mb.spt.expectations.ITestExpectation;

/**
 * Implementation of {@link ITestCase}.
 */
public class TestCase implements ITestCase {

    private final String description;
    private final Region descriptionRegion;
    private final ITestFragment fragment;
//    private final Resource resource;
    private final ListView<ITestExpectation> expectations;

    /**
     * Initializes a new instance of the {@link TestCase} class.
     *
     * @param description the description or name of the test case
     * @param descriptionRegion the source region covered by the test's description
     * @param fragment the fragment of this test case
     * @param resource the resource of the test suite from which this test case was extracted
     * @param expectations the test expectations for this test case
     */
    protected TestCase(
        String description,
        Region descriptionRegion,
        ITestFragment fragment,
//        Resource resource,
        ListView<ITestExpectation> expectations
    ) {
        this.description = description;
        this.descriptionRegion = descriptionRegion;
        this.fragment = fragment;
//        this.resource = resource;
        this.expectations = expectations;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Region getDescriptionRegion() {
        return descriptionRegion;
    }

    @Override
    public ITestFragment getFragment() {
        return fragment;
    }

//    @Override
//    public Resource getResource() {
//        return resource;
//    }

    @Override
    public ListView<ITestExpectation> getExpectations() {
        return expectations;
    }
}
