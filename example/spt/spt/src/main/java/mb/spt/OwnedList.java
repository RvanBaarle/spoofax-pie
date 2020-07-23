package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A list with an owner.
 *
 * @param <E> the type of elements in the list
 * @param <O> the type of owner
 */
public interface OwnedList<E, O> extends List<E> {

    /**
     * Gets the owner of the list.
     *
     * @return the owner; or {@code null} when it has none
     */
    public @Nullable O getOwner();

}
