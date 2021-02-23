package mb.strategies;

/**
 * An object whose string representation can be written to a {@link StringBuilder}.
 */
public interface Writable {

    /**
     * Writes this object to the given {@link StringBuilder}.
     *
     * @param sb the {@link StringBuilder}
     */
    default void writeTo(StringBuilder sb) {
        sb.append(this.toString());
    }

}
