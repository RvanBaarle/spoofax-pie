package mb.spt;

import mb.common.region.Region;
import mb.resource.Resource;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Builder for {@link ITestFragment} objects.
 */
public interface ITestFragmentBuilder {

    /**
     * Sets the region of the entire test fragment.
     *
     * @param region the region
     * @return this builder
     */
    ITestFragmentBuilder withRegion(Region region);

//    /**
//     * Sets the source file of the test suite from which the fragment was extracted.
//     *
//     * @param resource the resource
//     * @return this builder
//     */
//    ITestFragmentBuilder withResource(Resource resource);

    /**
     * Adds a selection of the fragment.
     *
     * Selections should be added in the order in which they appeared in the fragment.
     *
     * @param selection the selection
     * @return this builder
     */
    ITestFragmentBuilder addSelection(Region selection);

    /**
     * Adds a fragment piece.
     *
     * @param region the region of the fragment piece; or {@code null}
     * @param text the text of the fragment piece
     * @return this builder
     */
    ITestFragmentBuilder addPiece(@Nullable Region region, String text);

    /**
     * Builds the test fragment.
     *
     * After a call to {@link #build()}, the builder is reset and can be reused.
     *
     * @return the built test case
     */
    ITestFragment build();

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    ITestFragmentBuilder reset();

}
