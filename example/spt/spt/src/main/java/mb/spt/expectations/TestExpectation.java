package mb.spt.expectations;

import mb.common.region.Region;
import mb.spt.ITestCase;
import mb.spt.ITestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;

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
}
