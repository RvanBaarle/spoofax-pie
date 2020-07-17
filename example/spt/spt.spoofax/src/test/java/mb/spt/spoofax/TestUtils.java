package mb.spt.spoofax;

import mb.common.result.Result;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Utility functions for unit tests.
 */
public final class TestUtils {

    /**
     * Asserts that the given result is OK,
     * and prints the error message and stack trace otherwise.
     *
     * @param result the result to check
     * @param <T> the type of result
     * @param <E> the type of error
     * @return the result
     */
    public static <T, E extends Exception> T assertResultOk(Result<T, E> result) {
        assertTrue(result.isOk(), () -> {
            final @Nullable E exception = result.getErr();
            final StringWriter stringWriter = new StringWriter();
            try(final PrintWriter printWriter = new PrintWriter(stringWriter)) {
                exception.printStackTrace(printWriter);
            }
            return exception.getMessage() + ":\n" + stringWriter.toString();
        });
        return result.get();
    }

}
