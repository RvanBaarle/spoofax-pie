package mb.spt.spoofax;

import mb.common.util.ListView;
import mb.spt.ITestSuite;
import mb.spt.ITestSuiteBuilder;
import mb.spt.ITestSuiteElement;
import mb.spt.TestSuite;
import mb.spt.TestSuiteBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Implementation of {@link ISpoofaxTestSuiteBuilder}.
 */
public class SpoofaxTestSuiteBuilder extends TestSuiteBuilder implements ISpoofaxTestSuiteBuilder {

    protected @Nullable String startSymbol = null;
    protected @Nullable String languageName = null;

    @Override public ISpoofaxTestSuiteBuilder withName(String name) {
        return (ISpoofaxTestSuiteBuilder)super.withName(name);
    }

    @Override public ISpoofaxTestSuiteBuilder addTestElement(ITestSuiteElement element) {
        return (ISpoofaxTestSuiteBuilder)super.addTestElement(element);
    }

    @Override public ISpoofaxTestSuiteBuilder withStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
        return this;
    }

    @Override public ISpoofaxTestSuiteBuilder withLanguageName(String languageName) {
        this.languageName = languageName;
        return this;
    }

    @Override public ISpoofaxTestSuite build() {
        checkFieldNotNull("languageName", languageName);

        return new SpoofaxTestSuite(languageName, startSymbol, name, ListView.of(testElements));
    }

    @Override public ISpoofaxTestSuiteBuilder reset() {
        this.startSymbol = null;
        this.languageName = null;
        return (ISpoofaxTestSuiteBuilder)super.reset();
    }
}
