package mb.spt.expectations.spoofax;

import mb.spt.expectations.ITestExpectationBuilder;
import mb.spt.expectations.ITestExpectationExtractor;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * A test expectation extractor can recognize and extract a test expectation from a specification.
 *
 * This implementation is for Spoofax test specifications, which use ATerms to represent test expectations.
 *
 * @see mb.spt.expectations.ITestExpectationExtractor
 */
public interface ISpoofaxTestExpectationExtractor extends ITestExpectationExtractor<IStrategoTerm> {

    /**
     * Determines whwther this extractor can extract a test expectation from the given test expectation representation.
     *
     * @param expectationTerm the test expectation term
     * @return {@code true} when this extractor can extract the test expectation;
     * otherwise, {@code false}
     */
    boolean canExtract(IStrategoTerm expectationTerm);

    /**
     * Extracts a test expectation from the given test expectation representation.
     *
     * @param expectationTerm the test expectation term
     * @return a builder set up to build the given test expectation
     * @throws IllegalStateException this extractor cannot handle the given test expectation representation
     */
    ITestExpectationBuilder extract(IStrategoTerm expectationTerm);

}
