package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ITestFragmentBuilder}.
 */
public class TestFragmentBuilder implements ITestFragmentBuilder {

    protected @Nullable Region region = null;
    protected List<Region> selections = new ArrayList<>();
    protected List<ITestFragment.FragmentPiece> pieces = new ArrayList<>();

    @Override public ITestFragmentBuilder withRegion(Region region) {
        this.region = region;
        return this;
    }

    @Override public ITestFragmentBuilder addSelection(Region selection) {
        this.selections.add(selection);
        return this;
    }

    @Override
    public ITestFragmentBuilder addPiece(@Nullable Region region, String text) {
        this.pieces.add(new ITestFragment.FragmentPiece(region, text));
        return this;
    }

    @Override
    public ITestFragment build() {
        return new TestFragment(
            region,
            ListView.of(selections),
            ListView.of(pieces)
        );
    }

    @Override
    public ITestFragmentBuilder reset() {
        this.region = null;
        this.selections = new ArrayList<>();
        this.pieces = new ArrayList<>();
        return this;
    }
}
