package mb.spt;

import mb.common.region.Region;
import mb.common.util.ListView;
import mb.resource.Resource;
import mb.spt.expectations.ITestExpectation;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of {@link ITestCase}.
 */
public class TestCase implements ITestCase {

    private final String description;
    private final @Nullable Region descriptionRegion;
    private final ITestFragment fragment;
    private final ListView<ITestExpectation> expectations;

    private @Nullable ITestSuite owner;
    private int index;

    /**
     * Initializes a new instance of the {@link TestCase} class.
     *
     * @param description the description or name of the test case
     * @param descriptionRegion the source region covered by the test's description
     * @param fragment the fragment of this test case
     * @param resource the resource of the test suite from which this test case was extracted
     * @param expectations the test expectations for this test case
     */
    public TestCase(
        String description,
        @Nullable Region descriptionRegion,
        ITestFragment fragment,
        ListView<ITestExpectation> expectations
    ) {
        assert description != null;
        assert fragment != null;
        assert expectations != null;

        this.description = description;
        this.descriptionRegion = descriptionRegion;
        this.fragment = fragment;
        this.expectations = expectations;
    }

    @Override public @Nullable ITestSuite getTestSuite() {
        return owner;
    }

    @Override public String getDescription() {
        return description;
    }

    @Override public @Nullable Region getDescriptionRegion() {
        return descriptionRegion;
    }

    @Override public ITestFragment getFragment() {
        return fragment;
    }

    @Override public void setOwner(ITestSuite owner, int index) {
        if (owner != null && this.owner != owner)
            throw new IllegalArgumentException("Owner is already set.");
        if ((owner == null) == (index == -1))
            throw new IllegalArgumentException("Index must be -1 when owner is null, but positive or zero when owner is not null.");

        this.owner = owner;
        this.index = index;
    }

    @Override public ListView<ITestExpectation> getExpectations() {
        return expectations;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestCase other = (TestCase)o;
        return this.description.equals(other.description)
            && Objects.equals(this.descriptionRegion, other.descriptionRegion)
            && this.fragment.equals(other.fragment)
            && this.expectations.equals(other.expectations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            description,
            descriptionRegion,
            fragment,
            expectations
        ) + super.hashCode();
    }

    @Override public String toString() {
        return "TestCase{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "description='" + description + "', " +
                "descriptionRegion=" + descriptionRegion + ", " +
                "fragment=" + fragment + "', " +
                "expectations=" + expectations;
    }
}
