package mb.statix.common;

import com.google.common.collect.ImmutableList;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.common.strategies.InferStrategy;
import mb.statix.constraints.CUser;
import mb.statix.solver.IConstraint;
import mb.statix.solver.persistent.State;
import mb.statix.spec.Spec;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.ITermFactory;

import java.util.Collections;
import java.util.List;

/**
 * Statix-based semantic analyzer.
 */
public class StatixAnalyzer {

    private final Logger log;
    private final StrategoTerms strategoTerms;
    private final ITermFactory termFactory;
    private final StatixSpec spec;

    public StatixAnalyzer(
        StatixSpec spec,
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        this.spec = spec;
        this.termFactory = termFactory;
        this.strategoTerms = new StrategoTerms(termFactory);
        this.log = loggerFactory.create(getClass());
    }

    /**
     * Creates a new solver context.
     *
     * @return the solver context
     */
    public SolverContext createContext(StrategyEventHandler eventHandler) {
        return createContext(eventHandler, null);
    }

    /**
     * Creates a new solver context.
     *
     * @param focusVar the focus variable; or {@code null}
     * @return the solver context
     */
    public SolverContext createContext(StrategyEventHandler eventHandler, @Nullable ITermVar focusVar) {
        return new SolverContext(eventHandler, spec.getSpec(), focusVar, strategoTerms, Collections.emptyList(), null, null, null, null);
    }

    public SolverState createStartState(ITerm statixAst, String specName, String rootRuleName) {
        IConstraint rootConstraint = getRootConstraint(statixAst, specName, rootRuleName);
        final Spec specSpec = this.spec.getSpec();
        return SolverState.of(specSpec, State.of(), ImmutableList.of(rootConstraint), new SolutionMeta());
    }

    /**
     * Gets the root constraint of the specification.
     *
     * @return the root constraint
     */
    private IConstraint getRootConstraint(ITerm statixAst, String specName, String rootRuleName) {
        String qualifiedName = makeQualifiedName(specName, rootRuleName);
        return new CUser(qualifiedName, Collections.singletonList(statixAst), null);
    }

    /**
     * Returns the qualified name of the rule.
     *
     * @param specName the name of the specification
     * @param ruleName the name of the rule
     * @return the qualified name of the rule, in the form of {@code <specName>!<ruleName>}.
     */
    private String makeQualifiedName(String specName, String ruleName) {
        if (specName.equals("") || ruleName.contains("!")) return ruleName;
        return specName + "!" + ruleName;
    }

    /**
     * Invokes analysis.
     *
     * @param startState the start solver state
     * @return the resulting analysis result
     */
    public SolverState analyze(SolverContext ctx, SolverState startState) throws InterruptedException {
        final List<SolverState> states = InferStrategy.getInstance().eval(ctx, startState).toList().tryEval();
        if (states.isEmpty()) throw new IllegalStateException("This cannot be happening.");
        return states.get(0);
    }

}
