package mb.spt;

import mb.common.util.ListView;

import java.util.ArrayList;

/**
 * Fragment utility functions.
 */
public final class FragmentUtils {

    /**
     * Gets the textual pieces that make a complete fragment,
     * including any pieces from fixtures.
     *
     * @param testSuite the test suite that contains the test case
     * @param testCase the test case that contains the fragment
     * @param testFragment the test fragment
     * @return a list of fragment pieces
     */
    public static ListView<ITestFragment.FragmentPiece> getFragmentPieces(ITestSuite testSuite, ITestCase testCase, ITestFragment testFragment) {
        boolean found = false;
        ArrayList<ITestFragment.FragmentPiece> pieces = new ArrayList<>();
        for (ITestSuiteElement element : testSuite.getTestElements()) {
            if (element instanceof ITestFixture) {
                element.getFragment().getPieces().addAllTo(pieces);
            }
            if (element == testCase) {
                if (testCase.getFragment() != testFragment) {
                    throw new IllegalArgumentException("The given test fragment is not part of the given test case.");
                }
                found = true;
                testFragment.getPieces().addAllTo(pieces);
            }
        }
        if (!found) {
            throw new IllegalArgumentException("The given test case is not part of the given test suite.");
        }
        return ListView.of(pieces);
    }

    /**
     * Gets only the text of the fragment.
     *
     * @param testSuite the test suite that contains the test case
     * @param testCase the test case that contains the fragment
     * @param testFragment the test fragment
     * @return the fragment text
     */
    public static String getFragmentText(ITestSuite testSuite, ITestCase testCase, ITestFragment testFragment) {
        StringBuilder sb = new StringBuilder();
        for (ITestFragment.FragmentPiece piece : getFragmentPieces(testSuite, testCase, testFragment)) {
            sb.append(piece.getText());
        }
        return sb.toString();
    }

}
