package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Implementation of {@link ITestFixtureBuilder}.
 */
public class TestFixtureBuilder implements ITestFixtureBuilder {

    protected @Nullable OwnedList<? super ITestFixture, ITestSuite> container;
    protected @Nullable ITestFragment fragment = null;

    @Override
    public ITestSuiteElementBuilder withFragment(ITestFragment fragment) {
        this.fragment = fragment;
        return this;
    }

    @Override
    public ITestFixtureBuilder withContainer(OwnedList<? super ITestFixture, ITestSuite> container) {
        this.container = container;
        return this;
    }

    @Override
    public ITestFixture build() {
        checkFieldNotNull("container", container);
        checkFieldNotNull("fragment", fragment);
        final TestFixture fixture = new TestFixture(container, fragment);
        container.add(fixture);
        return fixture;
    }

    @Override
    public ITestFixtureBuilder reset() {
        this.container = null;
        this.fragment = null;
        return this;
    }
}
