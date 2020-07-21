package mb.spt.expectations;

/**
 * A test expectation extractor can recognize and extract a test expectation from a specification.
 *
 * Test expectation extractors must be registered to be able to be used. The registered extractors
 * are queried to find one that can handle the given test expectation. It is an error if more than one
 * extractor can handle a given test expectation.
 *
 * @param <R> the type of representation of the terms
 */
public interface ITestExpectationExtractor<R> {

    /**
     * Determines whether this extractor can extract a test expectation from the given test expectation representation.
     *
     * @param testExpectationRepresentation the test expectation representation
     * @return {@code true} when this extractor can extract the test expectation;
     * otherwise, {@code false}
     */
    boolean canExtract(R testExpectationRepresentation);

    /**
     * Extracts a test expectation from the given test expectation representation.
     *
     * @param testExpectationRepresentation the test expectation representation
     * @return a builder set up to build the given test expectation
     * @throws IllegalStateException this extractor cannot handle the given test expectation representation
     */
    ITestExpectationBuilder extract(R testExpectationRepresentation);

}
