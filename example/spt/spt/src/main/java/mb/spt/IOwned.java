package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A tree element that is owned.
 *
 * @param <O> the type of owner
 */
public interface IOwned<O> {

    /**
     * Gets the owner of this element.
     *
     * The owner is not part of any equality comparisons.
     *
     * @return the owner; or {@code null} when the element is not owned
     */
    @Nullable O getOwner();

    /**
     * Gets this element's index in the collection of the owner.
     *
     * @return the zero-based index; or -1 when the element has no index
     */
    int getIndex();

    /**
     * Sets the owner of this element.
     *
     * @param owner the owner of this element; or {@code null}
     * @param index the zero-based index of this element; or -1
     */
    void setOwner(O owner, int index);

}
