package mb.spt.extract;

import mb.spt.ITestSuite;

/**
 * Extract a test suite from an SPT test suite specification representation.
 *
 * @param <R> the type of representation
 */
public interface ITestSuiteExtractor<R> {

    ITestSuite extract(R representation);

}
