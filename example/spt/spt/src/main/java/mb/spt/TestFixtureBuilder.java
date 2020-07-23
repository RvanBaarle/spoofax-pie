package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Implementation of {@link ITestFixtureBuilder}.
 */
public class TestFixtureBuilder implements ITestFixtureBuilder {

    protected @Nullable ITestFragment fragment = null;

    @Override
    public ITestSuiteElementBuilder withFragment(ITestFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    public ITestFixture build() {
        checkFieldNotNull("fragment", fragment);
        return new TestFixture(fragment);
    }

    @Override
    public ITestFixtureBuilder reset() {
        this.fragment = null;
        return this;
    }
}
