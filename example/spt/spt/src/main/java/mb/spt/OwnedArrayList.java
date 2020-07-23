package mb.spt;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A list with an owner.
 *
 * @param <E> the type of elements in the list
 * @param <O> the type of owner
 */
public final class OwnedArrayList<E, O> extends ArrayList<E> implements OwnedList<E, O> {

    private @Nullable O owner = null;

    /**
     * Gets the owner of the list.
     *
     * @return the owner; or {@code null} when it has none
     */
    public @Nullable O getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable O owner) {
        this.owner = owner;
    }

    @Override public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        OwnedArrayList<?, ?> that = (OwnedArrayList<?, ?>)o;
        return this.owner.equals(that.owner)
            && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner);
    }

}
