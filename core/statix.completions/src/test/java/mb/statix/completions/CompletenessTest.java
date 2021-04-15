package mb.statix.completions;

import io.usethesource.capsule.Map;
import mb.jsglr.common.MoreTermUtils;
import mb.log.api.Logger;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.nabl2.terms.IApplTerm;
import mb.nabl2.terms.IStringTerm;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.resource.DefaultResourceKey;
import mb.resource.ResourceKey;
import mb.statix.common.PlaceholderVarMap;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StatixSpec;
import mb.statix.constraints.messages.IMessage;
import mb.statix.constraints.messages.MessageKind;
import mb.statix.solver.Delay;
import mb.statix.solver.IConstraint;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.DynamicTest;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests that the completion algorithm is complete.
 * For a given AST, it must be able to regenerate that AST in a number of completion steps,
 * when presented with the AST with a hole in it.
 */
@SuppressWarnings("SameParameterValue")
public abstract class CompletenessTest {

    private static final SLF4JLoggerFactory loggerFactory = new SLF4JLoggerFactory();
    private static final Logger log = loggerFactory.create(CompletenessTest.class);
    protected static final String TESTPATH = "/mb/statix/completions";


    /**
     * Creates a completion test.
     *
     * @param expectedTermPath the resource path to a file with the expected Stratego ATerm
     * @param inputTermPath the resource path to a file with the input Stratego ATerm
     * @param specPath the resource path to a file with the merged Statix spec Stratego ATerm
     * @param specName the name of the specification
     * @param rootRuleName the name of the root rule
     * @return the created test
     */
    protected DynamicTest completenessTest(String expectedTermPath, String inputTermPath, String specPath, String specName, String rootRuleName) {
        return DynamicTest.dynamicTest("complete file " + Paths.get(inputTermPath).getFileName() + " to " + Paths.get(expectedTermPath).getFileName() + " using spec " + Paths.get(specPath).getFileName() + "",
            () -> {
                StatixSpec spec = StatixSpec.fromClassLoaderResources(CompletenessTest.class, specPath);
                IStrategoTerm expectedTerm = MoreTermUtils.fromClassLoaderResources(CompletenessTest.class, expectedTermPath);
                IStrategoTerm inputTerm = MoreTermUtils.fromClassLoaderResources(CompletenessTest.class, inputTermPath);
                doCompletenessTest(expectedTerm, inputTerm, spec, specName, rootRuleName, expectedTermPath, inputTermPath);
            });
    }

    /**
     * Performs a completion test.
     *
     * @param expectedTerm the expected Stratego ATerm
     * @param inputTerm the input Stratego ATerm
     * @param spec the merged Statix spec Stratego ATerm
     * @param specName the name of the specification
     * @param rootRuleName the name of the root rule
     */
    private void doCompletenessTest(IStrategoTerm expectedTerm, IStrategoTerm inputTerm, StatixSpec spec, String specName, String rootRuleName, String expectedTermPath, String inputTermPath) throws InterruptedException, IOException {
        ITermFactory termFactory = new TermFactory();
        StrategoTerms strategoTerms = new StrategoTerms(termFactory);
        ResourceKey resourceKey = new DefaultResourceKey("test", "ast");

        IStrategoTerm annotatedExpectedTerm = StrategoTermIndices.index(expectedTerm, resourceKey.toString(), termFactory);
        ITerm expectedStatixTerm = strategoTerms.fromStratego(annotatedExpectedTerm);

        IStrategoTerm annotatedInputTerm = StrategoTermIndices.index(inputTerm, resourceKey.toString(), termFactory);
        ITerm inputStatixTerm = strategoTerms.fromStratego(annotatedInputTerm);

        doCompletenessTest(expectedStatixTerm, inputStatixTerm, spec, termFactory, resourceKey, specName, rootRuleName, expectedTermPath);
    }

    /**
     * Performs a completion test.
     *
     * @param expectedTerm the expected NaBL term
     * @param inputTerm the input NaBL term
     * @param spec the merged Statix spec
     * @param termFactory the Stratego term factory
     * @param resourceKey the resource key used to create term indices
     * @param specName the name of the specification
     * @param rootRuleName the name of the root rule
     */
    private void doCompletenessTest(ITerm expectedTerm, ITerm inputTerm, StatixSpec spec, ITermFactory termFactory, ResourceKey resourceKey, String specName, String rootRuleName, String testName) throws InterruptedException, IOException {
        StatsGatherer stats = new StatsGatherer();
        TermCompleter completer = new TermCompleter();
        StatixAnalyzer analyzer = new StatixAnalyzer(spec, termFactory, loggerFactory);

        // Preparation
        stats.startTest(testName);
//        long prepStartTime = System.nanoTime();
        PlaceholderVarMap placeholderVarMap = new PlaceholderVarMap(resourceKey.toString());
        CompletionExpectation<? extends ITerm> completionExpectation = CompletionExpectation.fromTerm(inputTerm, expectedTerm, placeholderVarMap);

//        long analyzeStartTime;
//        long completeStartTime;
//        int stepCount = 0;
//        int literalsInserted = 0;
        try(final StrategyEventHandler eventHandler = StrategyEventHandler.none()) {// new DebugEventHandler(Paths.get("debug.yml"))) {      // StrategyEventHandler.none()
            // Get the solver state of the program (whole project),
            // which should have some remaining constraints on the placeholders.
            SolverContext ctx = analyzer.createContext(eventHandler);
            stats.startInitialAnalysis();
//            analyzeStartTime = System.nanoTime();
            SolverState startState = analyzer.createStartState(completionExpectation.getIncompleteAst(), specName, rootRuleName)
                .withExistentials(placeholderVarMap.getVars())
                .precomputeCriticalEdges(ctx.getSpec());
            SolverState initialState = analyzer.analyze(ctx, startState);

            // We track the current collection of errors.
            final List<java.util.Map.Entry<IConstraint, IMessage>> currentErrors = initialState.getMessages().entrySet().stream().filter(kv -> kv.getValue().kind() == MessageKind.ERROR).collect(Collectors.toList());
            if(!currentErrors.isEmpty()) {
                log.warn("input program validation failed.\n"+ initialState);
                //fail("Completion failed: input program validation failed.\n" + initialState.toString());
                return;
            }

            if(initialState.getConstraints().isEmpty()) {
                fail("Completion failed: no constraints left, nothing to complete.\n" + initialState);
                return;
            }

            final SolverContext newCtx = ctx.withAllowedErrors(currentErrors);

            // We use a heuristic here.
            final Predicate<ITerm> isInjPredicate = t -> t instanceof IApplTerm && ((IApplTerm)t).getArity() == 1 && ((IApplTerm)t).getOp().contains("2");

//            completeStartTime = System.nanoTime();
            completionExpectation = completionExpectation.withState(initialState);
            // Perform a breadth-first search of completions:
            //  For each incomplete variable, we perform completion.
            //  If any of the variables result in one candidate, this candidate is applied.
            //  If none of the variables result in one candidate (i.e., there's no progress), then completion fails.
            while(!completionExpectation.isComplete()) {
                boolean allDelayed = true;

                // For each term variable, invoke completion
                for(ITermVar var : completionExpectation.getVars()) {
                    CompletionRunnable runnable = new CompletionRunnable(completer, completionExpectation, var, stats, newCtx, isInjPredicate);
                    CompletionResult result = runnable.get();
                    switch (result.state) {
                        case Success:
                            allDelayed = false;
                            completionExpectation = result.getCompletionExpectation();
                            break;
                        case Fail:
                            allDelayed = false;
                            return;
                        case Skip:
                            allDelayed = true;
                            break;
                        case Interrupted:
                            allDelayed = false;
                            log.warn("Interrupted.");
                            return;
                    }
                }

                if(allDelayed) {
                    // We've been skipping delayed variables but have made no progress. We're stuck.
                    @Nullable SolverState state = completionExpectation.getState();
                    fail(() -> "Stuck on delaying variables.\nState:\n  " + state);
                    return;
                }
            }
        }

        // Done! Success!
        stats.endTest();
//        long totalPrepareTime = analyzeStartTime - prepStartTime;
//        long totalAnalyzeTime = completeStartTime - analyzeStartTime;
//        long totalCompleteTime = System.nanoTime() - completeStartTime;
//        long avgDuration = totalCompleteTime / stepCount;
//        log.info("TEST DONE! Completing {} from {}.\n" +
//                "Completed {} steps in {} ms, avg. {} ms/step.\n" +
//                "Preparation: {} ms, initial analysis: {} ms.\n" +
//                "Inserted {} literals.",
//            expectedTermPath,
//            inputTermPath,
//            stepCount,
//            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalCompleteTime)),
//            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(avgDuration)),
//            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalPrepareTime)),
//            String.format("%2d", TimeUnit.NANOSECONDS.toMillis(totalAnalyzeTime)),
//            literalsInserted
//        );
    }

    private static boolean isVarInDelays(Map.Immutable<IConstraint, Delay> delays, ITermVar var) {
        return delays.keySet().stream().anyMatch(c -> c.getVars().contains(var));
    }

    private static boolean isLiteral(ITerm term) {
        // Is it a literal term, or an injection of a literal term?
        return term instanceof IStringTerm
            // TODO: Not use heuristics here.
            || (term instanceof IApplTerm && ((IApplTerm)term).getOp().contains("-LEX2"));
    }


    private static class CompletionRunnable implements Supplier<CompletionResult> {

        private final TermCompleter completer;
        private final CompletionExpectation<? extends ITerm> completionExpectation;
        private final ITermVar var;
        private final StatsGatherer stats;
        private final SolverContext newCtx;
        private final Predicate<ITerm> isInjPredicate;


        private CompletionRunnable(
            TermCompleter completer,
            CompletionExpectation<? extends ITerm> completionExpectation,
            ITermVar var,
            StatsGatherer stats,
            SolverContext newCtx, Predicate<ITerm> isInjPredicate
        ) {
            this.completer = completer;
            this.completionExpectation = completionExpectation;
            this.var = var;
            this.stats = stats;
            this.newCtx = newCtx;
            this.isInjPredicate = isInjPredicate;
        }


        @Override
        public CompletionResult get() {
            try {
                stats.startRound();
                final CompletionExpectation<? extends ITerm> newCompletionExpectation;
                final SolverState state = Objects.requireNonNull(completionExpectation.getState());

                if(isVarInDelays(state.getDelays(), var)) {
                    // We skip variables in delays, let's see where we get until we loop forever.
                    stats.skipRound();
                    return CompletionResult.skip();
//            } else {
//                allDelayed = false;
                }

                List<TermCompleter.CompletionSolverProposal> proposals = completer.complete(newCtx, isInjPredicate, state, var);
                // For each proposal, find the candidates that fit
                final CompletionExpectation<? extends ITerm> currentCompletionExpectation = completionExpectation;
//                log.info("------------------------------\n" +
//                    "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
//                    "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
//                    "State:\n  " + state);

                final List<CompletionExpectation<? extends ITerm>> candidates = proposals.stream()
                    .map(p -> currentCompletionExpectation.tryReplace(var, p))
                    .filter(Objects::nonNull).collect(Collectors.toList());
                if(candidates.size() == 1) {
                    // Only one candidate, let's apply it
                    newCompletionExpectation = candidates.get(0);
                    log.info("------------------------------\n" +
                        "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
                        "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
                        "State:\n  " + state +
                        "Got 1 candidate:\n  " + candidates.stream().map(c -> c.getState().toString()).collect(Collectors.joining("\n  ")));
                } else if(candidates.size() > 1) {
                    // Multiple candidates, let's use the one with the least number of open variables
                    // and otherwise the first one (could also use the biggest one instead)
                    candidates.sort(Comparator.comparingInt(o -> o.getVars().size()));
                    newCompletionExpectation = candidates.get(0);
                    log.info("------------------------------\n" +
                        "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
                        "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
                        "State:\n  " + state +
                        "Got " + candidates.size() + " candidates:\n  " + candidates.stream().map(c -> c.getState().toString()).collect(Collectors.joining("\n  ")));
                } else if(isLiteral(completionExpectation.getExpectations().get(var))) {
                    // No candidates, but the expected term is a string (probably the name of a declaration).
                    ITerm name = completionExpectation.getExpectations().get(var);
                    @Nullable CompletionExpectation<? extends ITerm> candidate = completionExpectation.tryReplace(var, new TermCompleter.CompletionSolverProposal(completionExpectation.getState(), name));
                    if(candidate == null) {
                        fail(() -> "------------------------------\n" +
                            "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
                            "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
                            "State:\n  " + state +
                            "Got NO candidates, but expected a literal. Could not insert literal " + name + ".\nProposals:\n  " + proposals.stream().map(p -> p.getTerm() + " <-  " + p.getNewState()).collect(Collectors.joining("\n  ")));
                        stats.endRound();
                        return CompletionResult.fail();
                    }
                    stats.insertedLiteral();
//                        literalsInserted += 1;
                    newCompletionExpectation = candidate;
                    log.info("------------------------------\n" +
                        "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
                        "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
                        "State:\n  " + state +
                        "Got 1 (literal) candidate:\n  " + candidate.getState());
                } else {
                    // No candidates, completion algorithm is not complete
                    fail(() -> "------------------------------\n" +
                        "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
                        "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
                        "State:\n  " + state +
                        "Got NO candidates.\nProposals:\n  " + proposals.stream().map(p -> p.getTerm() + " <-  " + p.getNewState()).collect(Collectors.joining("\n  ")));
                    stats.endRound();
                    return CompletionResult.fail();
                }
//                    stepCount += 1;
                stats.endRound();
                return CompletionResult.of(newCompletionExpectation);
            } catch(InterruptedException e) {
                return CompletionResult.interrupted();
            }
        }
    }


    private enum CompletionState {
        Success,
        Fail,
        Skip,
        Interrupted,
    }

    private static class CompletionResult {
        private final CompletionState state;
        private final CompletionExpectation<? extends ITerm> completionExpectation;

        public CompletionResult(CompletionState state, CompletionExpectation<? extends ITerm> completionExpectation) {
            this.state = state;
            this.completionExpectation = completionExpectation;
        }

        public CompletionExpectation<? extends ITerm> getCompletionExpectation() {
            return completionExpectation;
        }

        public CompletionState getState() {
            return state;
        }

        public static CompletionResult interrupted() { return new CompletionResult(CompletionState.Interrupted, null); }
        public static CompletionResult fail() { return new CompletionResult(CompletionState.Fail, null); }
        public static CompletionResult skip() { return new CompletionResult(CompletionState.Skip, null); }
        public static CompletionResult of(CompletionExpectation<? extends ITerm> completionExpectation) { return new CompletionResult(CompletionState.Success, completionExpectation); }

    }

}
