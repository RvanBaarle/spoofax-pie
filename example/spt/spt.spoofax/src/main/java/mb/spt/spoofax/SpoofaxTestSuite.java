package mb.spt.spoofax;

import mb.common.util.ListView;
import mb.spt.ITestSuite;
import mb.spt.ITestSuiteElement;
import mb.spt.TestSuite;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Implementation of {@link ISpoofaxTestSuite}.
 */
public class SpoofaxTestSuite extends TestSuite implements ISpoofaxTestSuite {

    private final String languageName;
    private final @Nullable String startSymbol;

    public SpoofaxTestSuite(
        String languageName,
        @Nullable String startSymbol,
        String name,
        ListView<ITestSuiteElement> testElements
    ) {
        super(name, testElements);
        assert languageName != null;

        this.startSymbol = startSymbol;
        this.languageName = languageName;
    }

    @Override public String getLanguageName() {
        return this.languageName;
    }

    @Override public @Nullable String getStartSymbol() {
        return this.startSymbol;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        SpoofaxTestSuite other = (SpoofaxTestSuite)o;
        return this.languageName.equals(other.languageName)
            && Objects.equals(this.startSymbol, other.startSymbol)
            && super.equals(other);
    }

    @Override public int hashCode() {
        return Objects.hash(languageName, startSymbol) + super.hashCode();
    }

    @Override public String toString() {
        return "SpoofaxTestSuite{" + fieldsToString() + "}";
    }

    @Override protected String fieldsToString() {
        return "languageName='" + languageName + "', "
            +  "startSymbol='" + startSymbol + "', "
            + super.fieldsToString();
    }
}
