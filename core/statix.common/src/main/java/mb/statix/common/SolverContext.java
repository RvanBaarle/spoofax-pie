package mb.statix.common;

import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.constraints.messages.IMessage;
import mb.statix.solver.IConstraint;
import mb.statix.spec.Spec;
import mb.strategies.Context;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;


/**
 * The context in which the search is performed.
 */
public final class SolverContext implements Context {

    private final StrategoTerms strategoTerms;
    private final Spec spec;
    @Nullable private final ITermVar focusVar;
    private final StrategyEventHandler eventHandler;
    private final Collection<Map.Entry<IConstraint, IMessage>> allowedErrors;

    private final Consumer<Long> expandPredicateReporter;
    private final Consumer<Long> expandInjectionReporter;
    private final Consumer<Long> expandQueryReporter;
    private final Consumer<Long> expandDeterministicReporter;

    /**
     * Initializes a new instance of the {@link SolverContext} class.
     *  @param eventHandler the event handler
     * @param spec the specification
     * @param focusVar the focus variable; or {@code null}
     * @param strategoTerms the stratego terms
     * @param expandPredicateReporter
     * @param expandInjectionReporter
     * @param expandQueryReporter
     * @param expandDeterministicReporter
     */
    public SolverContext(StrategyEventHandler eventHandler, Spec spec, @Nullable ITermVar focusVar, StrategoTerms strategoTerms, Collection<Map.Entry<IConstraint, IMessage>> allowedErrors, Consumer<Long> expandPredicateReporter, Consumer<Long> expandInjectionReporter, Consumer<Long> expandQueryReporter, Consumer<Long> expandDeterministicReporter) {
        this.eventHandler = eventHandler;
        this.spec = spec;
        this.focusVar = focusVar;
        this.strategoTerms = strategoTerms;
        this.allowedErrors = allowedErrors;
        this.expandPredicateReporter = expandPredicateReporter;
        this.expandInjectionReporter = expandInjectionReporter;
        this.expandQueryReporter = expandQueryReporter;
        this.expandDeterministicReporter = expandDeterministicReporter;
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
        return new SolverContext(eventHandler, spec, focus, strategoTerms, allowedErrors, expandPredicateReporter, expandInjectionReporter, expandQueryReporter, expandDeterministicReporter);
    }

    public SolverContext withAllowedErrors(Collection<Map.Entry<IConstraint, IMessage>> allowedErrors) {
        return new SolverContext(eventHandler, spec, focusVar, strategoTerms, allowedErrors, expandPredicateReporter, expandInjectionReporter, expandQueryReporter, expandDeterministicReporter);
    }

    public SolverContext withReporters(Consumer<Long> expandPredicateReporter, Consumer<Long> expandInjectionReporter, Consumer<Long> expandQueryReporter, Consumer<Long> expandDeterministicReporter) {
        return new SolverContext(eventHandler, spec, focusVar, strategoTerms, allowedErrors, expandPredicateReporter, expandInjectionReporter, expandQueryReporter, expandDeterministicReporter);
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

    @Override
    public Consumer<Long> getExpandPredicateReporter() {
        return this.expandPredicateReporter;
    }

    @Override
    public Consumer<Long> getExpandInjectionReporter() {
        return this.expandInjectionReporter;
    }

    @Override
    public Consumer<Long> getExpandQueryReporter() {
        return this.expandQueryReporter;
    }

    @Override
    public Consumer<Long> getExpandDeterministicReporter() {
        return this.expandDeterministicReporter;
    }

    @Override
    public Consumer<Long> getReporter(int index) {
        switch(index){
            case 0: return this.expandPredicateReporter;
            case 1: return this.expandInjectionReporter;
            case 2: return this.expandQueryReporter;
            case 3: return this.expandDeterministicReporter;
            default: return null;
        }
    }

    public Collection<Map.Entry<IConstraint, IMessage>> getAllowedErrors() {
        return allowedErrors;
    }
}
