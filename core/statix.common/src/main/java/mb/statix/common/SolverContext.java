package mb.statix.common;

import mb.nabl2.terms.ITermVar;
import mb.statix.spec.Spec;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * The context in which the search is performed.
 */
public final class SolverContext {

    private final Spec spec;
    @Nullable private final ITermVar focusVar;

    /**
     * Initializes a new instance of the {@link SolverContext} class.
     *
     * @param spec the specification
     * @param focusVar the focus variable; or {@code null}
     */
    public SolverContext(Spec spec, @Nullable ITermVar focusVar) {
        this.spec = spec;
        this.focusVar = focusVar;
    }

    /**
     * Gets the specification.
     *
     * @return the specification
     */
    public Spec getSpec() {
        return this.spec;
    }

    /**
     * Gets the focus variable.
     *
     * @return the focus variable; or {@code null}
     */
    @Nullable public ITermVar getFocusVar() {
        return this.focusVar;
    }
}
