package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of {@link ITestFixture}.
 */
public class TestFixture implements ITestFixture {

    private final ITestFragment fragment;

    private @Nullable ITestSuite owner;
    private int index;

    public TestFixture(ITestFragment fragment) {
        assert fragment != null;

        this.fragment = fragment;
    }

    @Override
    public ITestFragment getFragment() {
        return this.fragment;
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
