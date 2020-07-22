package mb.spt.spoofax;

import mb.spt.ITestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Spoofax test suite.
 */
public interface ISpoofaxTestSuite extends ITestSuite {

    /**
     * Gets the name of the language under test.
     *
     * @return the name of the language under test
     */
    String getLanguageName();

    /**
     * Gets the start symbol to use.
     *
     * @return the start symbol; or {@code null}
     */
    @Nullable String getStartSymbol();

}
