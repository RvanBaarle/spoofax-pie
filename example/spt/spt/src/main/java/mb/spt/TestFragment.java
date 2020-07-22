package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Implementation of {@link TestFragment}.
 */
public class TestFragment implements ITestFragment {

    private final @Nullable Region region;
    private final ListView<Region> selections;
//    private final Resource resource;
    private final ListView<FragmentPiece> pieces;

    /**
     * Initializes a new instance of the {@link TestFragment} class.
     *
     * @param region the region of the entire SPT Fragment node
     * @param selections the selections of this fragment
     * @param resource the source file of the test suite from which this fragment was extracted
     * @param text the text of this selection
     */
    protected TestFragment(
        @Nullable Region region,
        ListView<Region> selections,
//        Resource resource,
        ListView<FragmentPiece> pieces
    ) {
        this.region = region;
        this.selections = selections;
//        this.resource = resource;
        this.pieces = pieces;
    }

    @Override public @Nullable Region getRegion() {
        return region;
    }

    @Override public ListView<Region> getSelections() {
        return selections;
    }

    @Override public ListView<FragmentPiece> getPieces() {
        return pieces;
    }

//    @Override public Resource getResource() {
//        return resource;
//    }

}
