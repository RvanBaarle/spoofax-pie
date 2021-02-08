package mb.statix.search;

import java.util.Collection;

/**
 * Collection extensions.
 */
public final class CollectionExt {
    private CollectionExt() {}

    /**
     * Determines whether the collection contains any of the specified elements.
     *
     * @param collection the collection to check
     * @param elements the elements tp find
     * @param <E> the type of elements
     * @return {@code true} when the collection contains one or more of the given elements;
     * otherwise, {@code false}
     */
    public static <E> boolean containsAny(Collection<E> collection, Collection<E> elements) {
        for (E element : elements) {
            if (collection.contains(element)) return true;
        }
        return false;
    }
}
