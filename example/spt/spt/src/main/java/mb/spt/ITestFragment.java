package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;
import org.apache.commons.vfs2.FileObject;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment represents a piece of code within an SPT test suite, written in another language.
 *
 * Examples are the fragment of a test case, or the fragment of a 'parse to' test expectation. The former would be
 * written in the language under test, the latter possibly in another language.
 *
 * The problem with fragments is that the text has to be extracted from the original SPT specification, and parsed with
 * another language, while keeping the offsets of the nodes in the returned parse result correct within the larger
 * context of the SPT specification.
 *
 * The IFragment simply represents the SPT AST node and the selected regions within that node. These selected regions
 * have offsets based on the SPT AST, which is why parsing a fragment should be done using an IFragmentParser, to ensure
 * that the character offsets of the parse result match properly with the selected regions.
 */
public interface ITestFragment {

    /**
     * Gets the region of the entire test fragment.
     *
     * @return the region; or {@code null}
     */
    @Nullable Region getRegion();

    /**
     * Gets the selections of the fragment.
     *
     * The selections are ordered by the order in which they appeared in the fragment.
     *
     * @return the selections
     */
    // TODO: Make this a list of Selection objects?
    ListView<Region> getSelections();

//    /**
//     * Gets the source file of the test suite from which the fragment was extracted.
//     *
//     * @return the resource
//     */
//    Resource getResource();

    // NEW: A project is a Spoofax concept
//    /**
//     * Gets the project that contains the test suite that contains this fragment. It is required for analysis.
//     */
//    IProject getProject();

//    // TODO: What kind of name is this?
//    /**
//     * Gets the text of this selection. It is returned as tuples of an offset and a piece of text. The offset is the start
//     * offset of the piece of text in the rest of the Fragment's surrounding source (usually an SPT test suite).
//     *
//     * The text is a consecutive part of program text from the fragment. This text will not contain the SPT specific
//     * text.
//     *
//     * These tuples should be used by an IFragmentParser to ensure the parse result has the correct offsets.
//     */
//    ListView<FragmentPiece> getText();

    /**
     * Gets the textual pieces that make the fragment.
     *
     * @return a list of fragment pieces
     */
    ListView<FragmentPiece> getPieces();


    /**
     * A fragment piece.
     */
    public static class FragmentPiece {

        private final @Nullable Region region;
        private final String text;

        /**
         * Initializes a new instance of the {@link FragmentPiece} class.
         *
         * @param region the region of the fragment piece
         * @param text the text of the fragment piece
         */
        public FragmentPiece(@Nullable Region region, String text) {
            this.text = text;
            this.region = region;
        }

        /**
         * Gets the region of the piece.
         *
         * @return the region
         */
        public @Nullable Region getRegion() {
            return region;
        }

        /**
         * Gets the text of the piece.
         *
         * @return the text
         */
        public String getText() {
            return text;
        }

        @Override public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            FragmentPiece that = (FragmentPiece)o;
            return Objects.equals(region, that.region)
                && text.equals(that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(region, text);
        }

        @Override public String toString() {
            return "FragmentPiece{" +
                "region=" + region + "', " +
                "text='" + text + "'" +
                '}';
        }
    }

}
