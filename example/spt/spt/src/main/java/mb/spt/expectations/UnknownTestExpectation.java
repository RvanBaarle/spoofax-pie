package mb.spt.expectations;

import mb.common.region.Region;
import mb.common.util.ListView;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a test expectation for which not exactly one extractor could be found.
 *
 * This test expectation always fails with an error that the expectation is unknown.
 */
public final class UnknownTestExpectation implements ITestExpectation {

    private final @Nullable Region region;
    private final ListView<String> candidateNames;

    /**
     * Initializes a new instance of the {@link UnknownTestExpectation} class.
     *
     * @param region the syntax region of the test expectation
     */
    private UnknownTestExpectation(@Nullable Region region, ListView<String> candidateNames) {
        this.region = region;
        this.candidateNames = candidateNames;
    }

    @Override public @Nullable Region getRegion() {
        return region;
    }

    /**
     * Gets the test expectation extractors that where candidates for this expectation.
     *
     * When this list is empty, no extractors could handle the test expectation representation.
     * When this list is not empty, multiple extractors could handle the test expectation representation.
     *
     * @return a list of candidate test expectation extractors
     */
    public ListView<String> getCandidateNames() {
        return candidateNames;
    }

    /**
     * Special extractor for {@link UnknownTestExpectation}.
     *
     * Do NOT register this extractor with the other extractors!
     */
    public static class Extractor implements ITestExpectationExtractor<Object> {

        private final Iterable<String> candidateNames;

        public Extractor(Iterable<String> candidateNames) {
            this.candidateNames = candidateNames;
        }

        @Override public boolean canExtract(Object testExpectationRepresentation) {
            // Actually, this extractor can handle all representations,
            // but we don't want it to if it exer gets inadvertently registered with the other extractors.
            return false;
        }

        @Override public ITestExpectationBuilder extract(Object testExpectationRepresentation) {
            return new Builder().withCandidateNames(this.candidateNames);
        }
    }

    /**
     * Special builder for {@link UnknownTestExpectation}.
     */
    static class Builder implements ITestExpectationBuilder {

        private @Nullable Region region = null;
        private @Nullable ListView<String> candidateNames = null;

        @Override public ITestExpectationBuilder withRegion(@Nullable Region region) {
            this.region = region;
            return this;
        }

        public ITestExpectationBuilder withCandidateNames(Iterable<String> candidateNames) {
            candidateNames = ListView.copyOf(candidateNames);
            return this;
        }

        @Override public UnknownTestExpectation build() {
            final UnknownTestExpectation expectation = new UnknownTestExpectation(region, candidateNames);
            reset();
            return expectation;
        }

        @Override public ITestExpectationBuilder reset() {
            this.region = null;
            this.candidateNames = null;
            return this;
        }
    }
}
