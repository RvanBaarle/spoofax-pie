package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of {@link TestFragment}.
 */
public class TestFragment implements ITestFragment {

    private final @Nullable Region region;
    private final ListView<Region> selections;
    private final ListView<FragmentPiece> pieces;

    /**
     * Initializes a new instance of the {@link TestFragment} class.
     *
     * @param region the region of the entire SPT Fragment node
     * @param selections the selections of this fragment
     * @param resource the source file of the test suite from which this fragment was extracted
     * @param text the text of this selection
     */
    public TestFragment(
        @Nullable Region region,
        ListView<Region> selections,
        ListView<FragmentPiece> pieces
    ) {
        assert selections != null;
        assert pieces != null;

        this.region = region;
        this.selections = selections;
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

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestFragment that = (TestFragment)o;
        return Objects.equals(region, that.region) &&
            selections.equals(that.selections) &&
            pieces.equals(that.pieces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region, selections, pieces);
    }

    @Override public String toString() {
        return "TestFragment{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "region=" + region + ", " +
            "selections=" + selections + ", " +
            "pieces=" + pieces;
    }
}
