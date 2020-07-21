package mb.spt.expectations;

import mb.common.region.Region;
import mb.spt.ITestCase;
import mb.spt.ITestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a test expectation for a test case.
 */
public interface ITestExpectation {

    /**
     * Gets the syntax region of the test expectation.
     *
     * This is used for error reporting on the test expectation,
     * such as when it cannot be handled properly.
     *
     * @return the test expectation syntax region; or {@code null} when it is not known
     */
    @Nullable Region getRegion();

}
