package mb.spt;

import mb.common.util.ListView;

import java.util.Objects;

/**
 * Implementation of {@link ITestSuite}.
 */
public class TestSuite implements ITestSuite {

    private final String name;
    private final ListView<ITestSuiteElement> testElements;

    public TestSuite(String name, ListView<ITestSuiteElement> testElements) {
        assert name != null;
        assert testElements != null;

        this.name = name;
        this.testElements = testElements;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public ListView<ITestSuiteElement> getTestElements() {
        return this.testElements;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        TestSuite other = (TestSuite)o;
        return this.name.equals(other.name)
            && this.testElements.equals(other.testElements);
    }

    @Override public int hashCode() {
        return Objects.hash(name, testElements);
    }

    @Override public String toString() {
        return "TestSuite{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "name='" + name + "', " +
            "testElements=" + testElements;
    }
}
