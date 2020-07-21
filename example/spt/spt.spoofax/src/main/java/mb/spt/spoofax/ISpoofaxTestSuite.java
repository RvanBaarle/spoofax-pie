package mb.spt.spoofax;

import mb.spt.ITestSuite;

/**
 * A Spoofax test suite.
 */
public interface ISpoofaxTestSuite extends ITestSuite {

    /**
     * Gets the start symbol to use.
     *
     * @return the start symbol
     */
    String getStartSymbol();

    /**
     * Gets the name of the language under test.
     *
     * @return the name of the language under test
     */
    String getLanguageName();

}
