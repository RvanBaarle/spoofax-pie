package mb.spt.expectations.spoofax;

import mb.common.util.ListView;
import mb.spt.ITestFragment;
import mb.spt.ITestSuite;
import mb.spt.TestCase;
import mb.spt.TestFragment;
import mb.spt.spoofax.SpoofaxTestSuite;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ParseSucceedsExpectation} class.
 */
public final class ParseSucceedsExpectationTests {

    @Test
    public void test() {
        // Arrange
        final ParseSucceedsExpectation expectation = new ParseSucceedsExpectation(null);
        final ITestSuite testSuite = new SpoofaxTestSuite("Tiger", null, "test", ListView.of(
            new TestCase("parse ", null, new TestFragment(
                null,
                ListView.of(),
                ListView.of(
                    new ITestFragment.FragmentPiece(null, "\n  1\n")
                )
            ), ListView.of(
                expectation
            ))
        ));

        // Act
//        new ParseSucceedsExpectation.Evaluator();
        // TODO
    }

}
