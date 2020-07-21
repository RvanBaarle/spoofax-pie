package mb.spt.spoofax;

import mb.spt.ITestSuite;
import mb.spt.ITestSuiteBuilder;
import mb.spt.ITestSuiteElement;

/**
 * Builder for {@link ISpoofaxTestSuite} objects.
 */
public interface ISpoofaxTestSuiteBuilder extends ITestSuiteBuilder {

    @Override ISpoofaxTestSuiteBuilder withName(String name);

    @Override ISpoofaxTestSuiteBuilder addTestElement(ITestSuiteElement element);

    /**
     * Sets the start symbol to use.
     *
     * @param startSymbol the start symbol to use
     * @return this builder
     */
    ISpoofaxTestSuiteBuilder withStartSymbol(String startSymbol);

    /**
     * Sets the name of the language under test.
     *
     * @param languageName the name of the language under test
     * @return this builder
     */
    ISpoofaxTestSuiteBuilder withLanguageName(String languageName);

    @Override ISpoofaxTestSuite build();

    @Override ISpoofaxTestSuiteBuilder reset();

}
