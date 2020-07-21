package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;

/**
 * Implementation of {@link TestFragment}.
 */
public final class TestFragment implements ITestFragment {

    private final Region region;
    private final ListView<Region> selections;
    private final Resource resource;
    private final ListView<FragmentPiece> text;

    /**
     * Initializes a new instance of the {@link TestFragment} class.
     *
     * @param region the region of the entire SPT Fragment node
     * @param selections the selections of this fragment
     * @param resource the source file of the test suite from which this fragment was extracted
     * @param text the text of this selection
     */
    public TestFragment(
        Region region,
        ListView<Region> selections,
        Resource resource,
        ListView<FragmentPiece> text
    ) {
        this.region = region;
        this.selections = selections;
        this.resource = resource;
        this.text = text;
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public ListView<Region> getSelections() {
        return selections;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public ListView<FragmentPiece> getText() {
        return text;
    }
}
