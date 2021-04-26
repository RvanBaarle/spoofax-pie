package mb.statix.completions;

import com.google.common.collect.ImmutableSet;
import io.usethesource.capsule.Map;
import mb.jsglr.common.MoreTermUtils;
import mb.log.api.Level;
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
import mb.statix.spec.Rule;
import mb.statix.spec.Spec;
import mb.strategies.StrategyEventHandler;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.DynamicTest;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
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
    protected DynamicTest completenessTest(String expectedTermPath, String inputTermPath, String specPath, String specName, String csvPath, String rootRuleName) {
        return DynamicTest.dynamicTest("complete file " + Paths.get(inputTermPath).getFileName() + " to " + Paths.get(expectedTermPath).getFileName() + " using spec " + Paths.get(specPath).getFileName() + "",
            () -> {
                StatixSpec spec = StatixSpec.fromClassLoaderResources(CompletenessTest.class, specPath);
                IStrategoTerm expectedTerm = MoreTermUtils.fromClassLoaderResources(CompletenessTest.class, expectedTermPath);
                IStrategoTerm inputTerm = MoreTermUtils.fromClassLoaderResources(CompletenessTest.class, inputTermPath);
                doCompletenessTest(expectedTerm, inputTerm, spec, specName, rootRuleName, expectedTermPath, inputTermPath, csvPath);
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
    private void doCompletenessTest(IStrategoTerm expectedTerm, IStrategoTerm inputTerm, StatixSpec spec, String specName, String rootRuleName, String expectedTermPath, String inputTermPath, String csvPath) throws InterruptedException, IOException {
        ITermFactory termFactory = new TermFactory();
        StrategoTerms strategoTerms = new StrategoTerms(termFactory);
        ResourceKey resourceKey = new DefaultResourceKey("test", "ast");

        IStrategoTerm annotatedExpectedTerm = StrategoTermIndices.index(expectedTerm, resourceKey.toString(), termFactory);
        ITerm expectedStatixTerm = strategoTerms.fromStratego(annotatedExpectedTerm);

        IStrategoTerm annotatedInputTerm = StrategoTermIndices.index(inputTerm, resourceKey.toString(), termFactory);
        ITerm inputStatixTerm = strategoTerms.fromStratego(annotatedInputTerm);

        doCompletenessTest(expectedStatixTerm, inputStatixTerm, spec, termFactory, resourceKey, specName, rootRuleName, expectedTermPath, csvPath);
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
    private void doCompletenessTest(ITerm expectedTerm, ITerm inputTerm, StatixSpec spec, ITermFactory termFactory, ResourceKey resourceKey, String specName, String rootRuleName, String testName, String csvPath) throws InterruptedException, IOException {
        StatsGatherer stats = new StatsGatherer(csvPath);
        TermCompleter completer = new TermCompleter();
        StatixAnalyzer analyzer = new StatixAnalyzer(spec, termFactory, loggerFactory);
        precomputeOrderIndependentRules(spec.getSpec());
        ExecutorService executorService = Executors.newCachedThreadPool();

        // Preparation
        stats.startTest(testName);
        PlaceholderVarMap placeholderVarMap = new PlaceholderVarMap(resourceKey.toString());
        CompletionExpectation<? extends ITerm> completionExpectation = CompletionExpectation.fromTerm(inputTerm, expectedTerm, placeholderVarMap);

        try(final StrategyEventHandler eventHandler = StrategyEventHandler.none()) {// new DebugEventHandler(Paths.get("debug.yml"))) {
            // Get the solver state of the program (whole project),
            // which should have some remaining constraints on the placeholders.
            SolverContext ctx = analyzer.createContext(eventHandler).withReporters(
                t -> stats.reportSubTime(0, t),
                t -> stats.reportSubTime(1, t),
                t -> stats.reportSubTime(2, t),
                t -> stats.reportSubTime(3, t));
            stats.startInitialAnalysis();
            SolverState startState = analyzer.createStartState(completionExpectation.getIncompleteAst(), specName, rootRuleName)
                .withExistentials(placeholderVarMap.getVars())
                .precomputeCriticalEdges(ctx.getSpec());
            SolverState initialState = analyzer.analyze(ctx, startState);

            // We track the current collection of errors.
            final List<java.util.Map.Entry<IConstraint, IMessage>> currentErrors = initialState.getMessages().entrySet().stream().filter(kv -> kv.getValue().kind() == MessageKind.ERROR).collect(Collectors.toList());
            if(!currentErrors.isEmpty()) {
                //log.warn("input program validation failed.\n"+ initialState);
                fail("Completion failed: input program validation failed.\n" + initialState.toString());
                return;
            }

            if(initialState.getConstraints().isEmpty()) {
                fail("Completion failed: no constraints left, nothing to complete.\n" + initialState);
                return;
            }

            final SolverContext newCtx = ctx.withAllowedErrors(currentErrors);

            // We use a heuristic here.
            final Predicate<ITerm> isInjPredicate = t -> t instanceof IApplTerm && ((IApplTerm)t).getArity() == 1 && ((IApplTerm)t).getOp().contains("2");

            completionExpectation = completionExpectation.withState(initialState);

            // Perform a left-to-right depth-first search of completions:
            // - For each incomplete variable, we perform completion.
            // - If any of the variables result in one candidate, this candidate is applied.
            // - If none of the variables results in one candidate (i.e., there's no progress),
            //     then we try inserting a literal at the first available spot.
            // - If we cannot make progress and cannot insert literals,
            //     then completion fails.

            // List of failed variables
            final Set<ITermVar> failedVars = new HashSet<>();
            // List of delayed variables
            final Set<ITermVar> delayedVars = new HashSet<>();
            // Whether we did anything useful since the last time we tried all delays
            boolean progressedSinceDelays = false;
            while(!completionExpectation.isComplete()) {
                cleanup();

                // Pick the next variable that is not delayed or failed
                ITermVar var = completionExpectation.getVars().stream().filter(v -> !failedVars.contains(v) && !delayedVars.contains(v)).findFirst().orElse(null);
                if (var != null) {
                    CompletionRunnable runnable = new CompletionRunnable(completer, completionExpectation, var, stats, newCtx, isInjPredicate, testName);

                    Future<CompletionResult> future = executorService.submit(runnable);
                    try {
                        CompletionResult result = future.get(60, TimeUnit.SECONDS);
                        switch(result.state) {
                            case Success:
                                progressedSinceDelays = true;
                                completionExpectation = result.getCompletionExpectation();
                                break;
                            case Skip:
                                log.info("Delayed {}", var);
                                delayedVars.add(var);
                                break;
                            case Fail:
                                log.info("Failed {}", var);
                                failedVars.add(var);
                                break;
                        }
                    } catch(TimeoutException ex) {
                        fail(() -> "Interrupted.");
                        return;
                    } catch(ExecutionException ex) {
                        log.error("Error was thrown: " + ex.getMessage(), ex);
                        fail(() -> "Error was thrown.");
                        return;
                    }
                } else if (progressedSinceDelays && !delayedVars.isEmpty()) {
                    // Try all delayed variables again
                    log.warn("All variables delayed, trying again.");
                    delayedVars.clear();
                    progressedSinceDelays = false;
                    continue;
                } else {
                    log.warn("All variables delayed or rejected, trying to insert literals.");
                    // All of the completions failed previously
                    // Let's try to insert a literal
                    CompletionExpectation<? extends ITerm> finalCompletionExpectation = completionExpectation;
                    ITermVar literalVar = completionExpectation.getVars().stream().filter(v -> isLiteral(finalCompletionExpectation.getExpectations().get(v))).findFirst().orElse(null);
                    if (literalVar != null) {
                        // Insert the literal
                        ITerm value = completionExpectation.getExpectations().get(literalVar);
                        @Nullable CompletionExpectation<? extends ITerm> candidate = completionExpectation.tryReplace(literalVar, new TermCompleter.CompletionSolverProposal(completionExpectation.getState(), value));
                        if(candidate == null) {
                            logCompletionStepResult(Level.Error, "Could not insert literal '" + value + "'.", testName, literalVar, completionExpectation);
                            stats.endRound();
                            fail("Could not insert literal '" + value + "'.");
                            break;
                        }
                        progressedSinceDelays = true;
                        stats.insertedLiteral();
                        completionExpectation = candidate;
                        logCompletionStepResultWithCandidates(Level.Info, "Inserted literal '" + value + "'.", testName, literalVar, completionExpectation, Collections.singletonList(candidate));
                    } else {
                        // No literals to insert
                        fail("All completions failed and could not insert any literals.");
                        break;
                    }

                    // Try again on all completion variables
                    failedVars.clear();
                    continue;
                }
            }
            log.info("Done completing!");


//            // Perform a breadth-first search of completions:
//            //  For each incomplete variable, we perform completion.
//            //  If any of the variables result in one candidate, this candidate is applied.
//            //  If none of the variables result in one candidate (i.e., there's no progress), then completion fails.
//            while(!completionExpectation.isComplete()) {
//                boolean allDelayed = true;
//
//                // For each term variable, invoke completion
//                for(ITermVar var : completionExpectation.getVars()) {
//                    cleanup();
//
//                    CompletionRunnable runnable = new CompletionRunnable(completer, completionExpectation, var, stats, newCtx, isInjPredicate, testName);
//
//                    Future<CompletionResult> future = executorService.submit(runnable);
//                    try {
//                        CompletionResult result = future.get(60, TimeUnit.SECONDS);
//                        switch(result.state) {
//                            case Success:
//                                allDelayed = false;
//                                completionExpectation = result.getCompletionExpectation();
//                                break;
//                            case Skip:
//                                allDelayed = true;
//                                break;
//                            case Fail:
//                                fail("Failed.");
//                                return;
//                        }
//                    } catch (TimeoutException ex) {
//                        fail(() -> "Interrupted.");
//                        return;
//                    } catch(ExecutionException ex) {
//                        log.error("Error was thrown: " + ex.getMessage(), ex);
//                        fail(() -> "Error was thrown.");
//                        return;
//                    }
//                }
//
//                if(allDelayed) {
//                    // We've been skipping delayed variables but have made no progress. We're stuck.
//                    @Nullable SolverState state = completionExpectation.getState();
//                    fail(() -> "Stuck on delaying variables.\nState:\n  " + state);
//                    return;
//                }
//            }
        }

        // Done! Success!
        stats.endTest();
    }

    private void precomputeOrderIndependentRules(Spec spec) {
        log.info("Precomputing...");
        long start = System.nanoTime();
        for(String ruleName : spec.rules().getRuleNames()) {
            spec.rules().getOrderIndependentRules(ruleName);
        }
        log.info("Precomputed order independent rules in " + ((System.nanoTime() - start) / 1000000) + " ms");

    }

    private static void logCompletionStepResult(Level level, String message, String testName, ITermVar var, CompletionExpectation<?> expectation) {
        log.log(level, "-------------- " + testName +" ----------------\n" +
            "Complete var " + var + " in AST:\n  " + expectation.getIncompleteAst() + "\n" +
            "Expected:\n  " + expectation.getExpectations().get(var), //+ "\n" +
            "State:\n  " + expectation.getState() +
            message);
    }

    private static void logCompletionStepResultWithProposals(Level level, String message, String testName, ITermVar var, CompletionExpectation<?> expectation, List<TermCompleter.CompletionSolverProposal> proposals) {
        logCompletionStepResult(level, message + "\nProposals:\n  " + proposals.stream().map(p -> p.getTerm() + " <-  " + p.getNewState()).collect(Collectors.joining("\n  ")), testName, var, expectation);
    }

    private static void logCompletionStepResultWithCandidates(Level level, String message, String testName, ITermVar var, CompletionExpectation<?> expectation, List<CompletionExpectation<? extends ITerm>> candidates) {
        logCompletionStepResult(level, message + "\nGot " + candidates.size() + " candidate" + (candidates.size() == 1 ? "" : "s") + ":\n  " + candidates.stream().map(c -> c.getState().toString()).collect(Collectors.joining("\n  ")), testName, var, expectation);
    }
    // List<TermCompleter.CompletionSolverProposal> proposals

    private static void cleanup() {
        log.info("Cleaning...");
        long cleanStart = System.nanoTime();
        System.gc();
        System.runFinalization();
        System.gc();
        log.info("Cleaned in " + ((System.nanoTime() - cleanStart) / 1000000) + " ms");
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        log.info("Free memory: {} MB", freeMemory / (1024 * 1024));
        log.info("Allocated memory: {} MB", allocatedMemory / (1024 * 1024));
        log.info("Max memory: {} MB", maxMemory / (1024 * 1024));
        log.info("Total free memory: {} MB", (freeMemory + (maxMemory - allocatedMemory)) / (1024 * 1024));
    }

    private static boolean isVarInDelays(Map.Immutable<IConstraint, Delay> delays, ITermVar var) {
        return delays.values().stream().anyMatch(d -> d.vars().contains(var));
//        return delays.keySet().stream().anyMatch(c -> c.getVars().contains(var));
    }

    private static boolean isLiteral(ITerm term) {
        // Is it a literal term, or an injection of a literal term?
        return term instanceof IStringTerm
            // TODO: Not use heuristics here.
            || (term instanceof IApplTerm && ((IApplTerm)term).getOp().contains("-LEX2"));
    }


    private static class CompletionRunnable implements Callable<CompletionResult> {

        private static final Logger log = loggerFactory.create(CompletionRunnable.class);
        private final TermCompleter completer;
        private final CompletionExpectation<? extends ITerm> completionExpectation;
        private final ITermVar var;
        private final StatsGatherer stats;
        private final SolverContext newCtx;
        private final Predicate<ITerm> isInjPredicate;
        private final String testName;


        private CompletionRunnable(
            TermCompleter completer,
            CompletionExpectation<? extends ITerm> completionExpectation,
            ITermVar var,
            StatsGatherer stats,
            SolverContext newCtx,
            Predicate<ITerm> isInjPredicate,
            String testName
        ) {
            this.completer = completer;
            this.completionExpectation = completionExpectation;
            this.var = var;
            this.stats = stats;
            this.newCtx = newCtx;
            this.isInjPredicate = isInjPredicate;
            this.testName = testName;
        }


        @Override
        public CompletionResult call() throws InterruptedException {
            try {
                stats.startRound();
                final CompletionExpectation<? extends ITerm> newCompletionExpectation;
                final SolverState state = Objects.requireNonNull(completionExpectation.getState());

                log.info("====================== " + testName +" ================================\n" +
                    "COMPLETING var " + var + " in AST:\n  " + completionExpectation.getIncompleteAst() + "\n" +
                    "Expected:\n  " + completionExpectation.getExpectations().get(var) + "\n" +
                    "State:\n  " + state);

                if(isVarInDelays(state.getDelays(), var)) {
                    // We skip variables in delays, let's see where we get until we loop forever.
                    stats.skipRound();
                    log.info("All delayed. Skipped.");
                    return CompletionResult.skip();
                }

                List<TermCompleter.CompletionSolverProposal> proposals = completer.complete(newCtx, isInjPredicate, state, var);
                // For each proposal, find the candidates that fit
                final CompletionExpectation<? extends ITerm> currentCompletionExpectation = completionExpectation;

                final List<CompletionExpectation<? extends ITerm>> candidates = proposals.stream()
                    .map(p -> currentCompletionExpectation.tryReplace(var, p))
                    .filter(Objects::nonNull).collect(Collectors.toList());
                if(candidates.size() == 1) {
                    // Only one candidate, let's apply it
                    newCompletionExpectation = candidates.get(0);
                    logCompletionStepResultWithCandidates(Level.Info, "", testName, var, currentCompletionExpectation, candidates);
                } else if(candidates.size() > 1) {
                    // Multiple candidates, let's use the one with the least number of open variables
                    // and otherwise the first one (could also use the biggest one instead)
                    candidates.sort(Comparator.comparingInt(o -> o.getVars().size()));
                    newCompletionExpectation = candidates.get(0);
                    logCompletionStepResultWithCandidates(Level.Info, "", testName, var, currentCompletionExpectation, candidates);
//                } else if(isLiteral(completionExpectation.getExpectations().get(var))) {
//                    // No candidates, but the expected term is a string (probably the name of a declaration).
//                    ITerm name = completionExpectation.getExpectations().get(var);
//                    @Nullable CompletionExpectation<? extends ITerm> candidate = completionExpectation.tryReplace(var, new TermCompleter.CompletionSolverProposal(completionExpectation.getState(), name));
//                    if(candidate == null) {
//                        log.info("-------------- " + testName +" ----------------\n" +
//                            "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
//                            "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
//                            //"State:\n  " + state +
//                            "Got NO candidates, but expected a literal. Could not insert literal " + name + ".\nProposals:\n  " + proposals.stream().map(p -> p.getTerm() + " <-  " + p.getNewState()).collect(Collectors.joining("\n  ")));
//                        stats.endRound();
//                        return CompletionResult.fail();
//                    }
//                    stats.insertedLiteral();
//                    newCompletionExpectation = candidate;
//                    log.info("-------------- " + testName +" ----------------\n" +
//                        "Complete var " + var + " in AST:\n  " + currentCompletionExpectation.getIncompleteAst() + "\n" +
//                        "Expected:\n  " + currentCompletionExpectation.getExpectations().get(var) + "\n" +
//                        //"State:\n  " + state +
//                        "Got 1 (literal) candidate:\n  " + candidate.getState());
                } else {
                    // No candidates, completion algorithm is not complete
                    logCompletionStepResultWithProposals(Level.Warn, "Got NO candidates.", testName, var, currentCompletionExpectation, proposals);
                    stats.endRound();
                    return CompletionResult.fail();
                }
                stats.endRound();
                return CompletionResult.of(newCompletionExpectation);
            } catch(Throwable ex) {
                log.error("Uncaught exception: " + ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    private enum CompletionState {
        Success,
        Fail,
        Skip,
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

        public static CompletionResult fail() { return new CompletionResult(CompletionState.Fail, null); }
        public static CompletionResult skip() { return new CompletionResult(CompletionState.Skip, null); }
        public static CompletionResult of(CompletionExpectation<? extends ITerm> completionExpectation) { return new CompletionResult(CompletionState.Success, completionExpectation); }

    }

}
