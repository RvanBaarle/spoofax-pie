package mb.statix.common;

import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.spec.Spec;
import mb.strategies.Context;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * The context in which the search is performed.
 */
public final class SolverContext implements Context {

    private final StrategoTerms strategoTerms;
    private final Spec spec;
    @Nullable private final ITermVar focusVar;
    private final StrategyEventHandler eventHandler;

    /**
     * Initializes a new instance of the {@link SolverContext} class.
     *
     * @param eventHandler the event handler
     * @param spec the specification
     * @param focusVar the focus variable; or {@code null}
     * @param strategoTerms the stratego terms
     */
    public SolverContext(StrategyEventHandler eventHandler, Spec spec, @Nullable ITermVar focusVar, StrategoTerms strategoTerms) {
        this.eventHandler = eventHandler;
        this.spec = spec;
        this.focusVar = focusVar;
        this.strategoTerms = strategoTerms;
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

    public SolverContext withFocusVar(@Nullable ITermVar focus) {
        return new SolverContext(eventHandler, spec, focus, strategoTerms);
    }

    /**
     * Gets the stratego terms.
     *
     * @return the stratego terms
     */
    public StrategoTerms getStrategoTerms() { return this.strategoTerms; }

    @Override public StrategyEventHandler getEventHandler() {
        return this.eventHandler;
    }
}
