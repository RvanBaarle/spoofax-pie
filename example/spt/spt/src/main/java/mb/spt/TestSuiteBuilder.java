package mb.spt;

import mb.common.util.ListView;
import mb.spt.extract.ITestSuiteExtractor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ITestSuiteBuilder}.
 */
public class TestSuiteBuilder implements ITestSuiteBuilder {

    protected @Nullable String name = null;
    protected List<ITestSuiteElement> testElements = new ArrayList<>();

    @Override public ITestSuiteBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @Override public ITestSuiteBuilder addTestElement(ITestSuiteElement element) {
        this.testElements.add(element);
        return this;
    }

    @Override public ITestSuite build() {
        final TestSuite testSuite = new TestSuite(name, ListView.of(testElements));
        for (int i = 0; i < testElements.size(); i++) {
            ITestSuiteElement element = testElements.get(i);
            element.setOwner(testSuite, i);
        }
        return testSuite;
    }

    @Override public ITestSuiteBuilder reset() {
        this.name = null;
        this.testElements = new ArrayList<>();
        return this;
    }
}
