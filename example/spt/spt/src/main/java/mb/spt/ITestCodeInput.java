package mb.spt;

import mb.common.util.ListView;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Code input.
 */
public interface ITestCodeInput extends ITestInput {

    /**
     * Gets the name of the language of the code.
     *
     * @return the language name
     */
    String getLanguageName();

    /**
     * Gets the code fragments.
     *
     * Code fragments include the test case fragment itself,
     * but also any fixture fragments. The order of the fragments
     * determines the order in which they are concatenated into one.
     *
     * @return the code fragments
     */
    ListView<ITestFragment> getFragments();

    /**
     * Gets the textual pieces that make a complete code input.
     *
     * @return a list of code fragment pieces
     */
    public default ListView<ITestFragment.FragmentPiece> getPieces() {
        return ListView.of(getFragments().stream().flatMap(f -> f.getPieces().stream()).collect(Collectors.toList()));
    }

    /**
     * Gets the full text of the code input.
     *
     * @return the code input text
     */
    public default String getText() {
        StringBuilder sb = new StringBuilder();
        for (ITestFragment.FragmentPiece piece : getPieces()) {
            sb.append(piece.getText());
        }
        return sb.toString();
    }

}
