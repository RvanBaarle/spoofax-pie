package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of {@link ITestFixture}.
 */
public class TestFixture implements ITestFixture {

    private final OwnedList<? super ITestFixture, ITestSuite> container;
    private final ITestFragment fragment;

    private @Nullable ITestSuite owner;
    private int index;

    public TestFixture(OwnedList<? super ITestFixture, ITestSuite> container, ITestFragment fragment) {
        assert container != null;
        assert fragment != null;

        this.container = container;
        this.fragment = fragment;
    }

    @Override public @Nullable ITestSuite getTestSuite() {
        return this.container.getOwner();
    }

    @Override
    public ITestFragment getFragment() {
        return this.fragment;
    }

    @Override public void setOwner(ITestSuite owner, int index) {
        if (owner != null && this.owner != owner)
            throw new IllegalArgumentException("Owner is already set.");
        if ((owner == null) == (index == -1))
            throw new IllegalArgumentException("Index must be -1 when owner is null, but positive or zero when owner is not null.");

        this.owner = owner;
        this.index = index;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestFixture that = (TestFixture)o;
        return fragment.equals(that.fragment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragment);
    }

    @Override public String toString() {
        return "TestFixture{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "fragment=" + fragment + "";
    }

}
