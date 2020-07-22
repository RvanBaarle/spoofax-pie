package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.spt.expectations.ITestExpectation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Implementation of {@link ITestCaseBuilder}.
 */
public class TestCaseBuilder implements ITestCaseBuilder {

    protected @Nullable String description = null;
    protected @Nullable Region descriptionRegion = null;
    protected List<ITestExpectation> expectations = new ArrayList<>();
    protected @Nullable ITestFragment fragment = null;

    @Override public ITestCaseBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override public ITestCaseBuilder withDescriptionRegion(@Nullable Region descriptionRegion) {
        this.descriptionRegion = this.descriptionRegion;
        return this;
    }

    @Override public ITestCaseBuilder addTestExpectation(ITestExpectation testExpectation) {
        this.expectations.add(testExpectation);
        return this;
    }

    @Override public ITestCaseBuilder withFragment(ITestFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    public ITestCase build() {
        checkFieldNotNull("description", description);
        checkFieldNotNull("fragment", fragment);

        return new TestCase(
            description,
            descriptionRegion,
            fragment,
            ListView.of(expectations)
        );
    }

    @Override public ITestCaseBuilder reset() {
        this.description = null;
        this.descriptionRegion = null;
        this.expectations = new ArrayList<>();
        this.fragment = null;

        return this;
    }
}
