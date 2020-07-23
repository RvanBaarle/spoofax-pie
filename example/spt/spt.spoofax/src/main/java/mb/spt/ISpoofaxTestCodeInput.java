package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ISpoofaxTestCodeInput extends ITestCodeInput {

    /**
     * Gets the name of the start symbol.
     *
     * @return the name of the start symbol; or {@code null} when not specified
     */
    @Nullable String getStartSymbol();

}
