package mb.spt;

import jdk.nashorn.internal.ir.annotations.Immutable;
import mb.common.message.Message;
import mb.common.util.ListView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A test suite.
 */
public interface ITestSuite {

    /**
     * Gets the name of the test suite.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the test elements in the test suite.
     *
     * @return the test elements
     */
    ListView<ITestSuiteElement> getTestElements();

    /**
     * Gets the test cases in the test suite.
     *
     * @return the test cases
     */
    default ListView<ITestCase> getTestCases() {
        return ListView.of(getTestElements().stream()
            .filter(ITestCase.class::isInstance)
            .map(ITestCase.class::cast)
            .collect(Collectors.toList())
        );
    }

}
