package mb.spt.spoofax;

import mb.common.util.ListView;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.spt.ITestFragment;
import mb.spt.ITestSuite;
import mb.spt.TestCase;
import mb.spt.TestCaseBuilder;
import mb.spt.TestFixtureBuilder;
import mb.spt.TestFragment;
import mb.spt.TestFragmentBuilder;
import mb.spt.TestSuite;
import mb.spt.expectations.ITestExpectationExtractor;
import mb.spt.expectations.spoofax.ParseSucceedsExpectation;
import mb.spt.expectations.spoofax.ParseToAtermTestExpectation;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.io.TAFTermReader;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link SpoofaxTestSuiteExtractor} class.
 */
public final class SpoofaxTestSuiteExtractorTests {

    @Test
    public void extractTestSuite() {
        // Arrange
        final HashSet<ITestExpectationExtractor<IStrategoTerm>> expectationExtractors = new HashSet<>();
        expectationExtractors.add(new ParseToAtermTestExpectation.Extractor());
        expectationExtractors.add(new ParseSucceedsExpectation.Extractor());
        final SpoofaxTestSuiteExtractor sut = new SpoofaxTestSuiteExtractor(
            new SLF4JLoggerFactory(),
            new NullTermTracer(),
            expectationExtractors,
            new SpoofaxTestSuiteBuilder(),
            new TestCaseBuilder(),
            new TestFragmentBuilder(),
            new TestFixtureBuilder()
        );
        final IStrategoTerm term = new TAFTermReader(new TermFactory()).parseFromString("TestSuite(\n" +
            "  [Name(\"test\"), Language(\"Tiger\")]\n" +
            ", None()\n" +
            ", [Test(\n" +
            "     \"parse \"\n" +
            "   , \"[[\"\n" +
            "   , Fragment(\"\\n  1\\n\", Done())\n" +
            "   , \"]]\"\n" +
            "   , [ParseSucceeds()]\n" +
            "   )]\n" +
            ")");

        // Act
        final ITestSuite actualTestSuite = sut.extract(term);

        // Assert
        final ITestSuite expectedTestSuite = new SpoofaxTestSuite("Tiger", null, "test", ListView.of(
            new TestCase("parse ", null, new TestFragment(
                null,
                ListView.of(),
                ListView.of(
                    new ITestFragment.FragmentPiece(null, "\n  1\n")
                )
            ), ListView.of(
                new ParseSucceedsExpectation(null)
            ))
        ));
        assertEquals(expectedTestSuite, actualTestSuite);
    }

}
