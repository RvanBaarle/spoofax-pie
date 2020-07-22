package mb.spt.expectations;

import mb.common.region.Region;
import mb.spt.ITestCase;
import mb.spt.ITestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Base class for {@link ITestExpectation} implementations.
 */
public abstract class TestExpectation implements ITestExpectation {

    private final @Nullable Region region;

    /**
     * Initializes a new instance of the {@link TestExpectation} class.
     *
     * @param region the region of the test expectation
     */
    protected TestExpectation(@Nullable Region region) {
        this.region = region;
    }

    @Override public @Nullable Region getRegion() {
        return region;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestExpectation that = (TestExpectation)o;
        return Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region);
    }

    @Override public String toString() {
        return "TestExpectation{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "region=" + region + "";
    }
}
