package mb.spt.spoofax;

import mb.common.util.ListView;
import mb.spt.ITestSuite;
import mb.spt.ITestSuiteElement;
import mb.spt.TestSuite;

/**
 * Implementation of {@link ISpoofaxTestSuite}.
 */
public final class SpoofaxTestSuite extends TestSuite implements ISpoofaxTestSuite {

    private final String startSymbol;
    private final String languageName;

    protected SpoofaxTestSuite(
        String startSymbol,
        String languageName,
        String name,
        ListView<ITestSuiteElement> testElements
    ) {
        super(name, testElements);
        this.startSymbol = startSymbol;
        this.languageName = languageName;
    }

    @Override public String getStartSymbol() {
        return this.startSymbol;
    }

    @Override public String getLanguageName() {
        return this.languageName;
    }
}
