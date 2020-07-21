package mb.spt.expectations;

/**
 * Utility functions for builders.
 */
public final class BuilderUtils {

    /**
     * Asserts that the given value is not {@code null}.
     *
     * @param fieldName the name of the field being checked
     * @param value the value to check
     * @throws IllegalStateException the value is null
     */
    public static void checkFieldNotNull(String fieldName, Object value) {
        if (value == null) {
            throw new IllegalStateException("The field " + fieldName + " was not set.");
        }
    }

}
