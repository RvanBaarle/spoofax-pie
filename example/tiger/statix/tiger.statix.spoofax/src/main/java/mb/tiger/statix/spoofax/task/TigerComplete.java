package mb.tiger.statix.spoofax.task;

import com.google.common.collect.ImmutableList;
import mb.common.editing.TextEdit;
import mb.common.region.Region;
import mb.common.style.StyleName;
import mb.common.util.ListView;
import mb.completions.common.CompletionItem;
import mb.completions.common.CompletionResult;
import mb.log.api.Logger;
import mb.log.api.LoggerFactory;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.ListTerms;
import mb.nabl2.terms.Terms;
import mb.nabl2.terms.stratego.StrategoTermIndices;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.nabl2.terms.stratego.TermOrigin;
import mb.nabl2.terms.stratego.TermPlaceholder;
import mb.pie.api.ExecContext;
import mb.pie.api.ExecException;
import mb.pie.api.Function;
import mb.pie.api.Supplier;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import mb.statix.common.PlaceholderVarMap;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.StatixAnalyzer;
import mb.statix.common.StrategoPlaceholders;
import mb.statix.completions.TermCompleter;
import mb.statix.solver.persistent.State;
import mb.tiger.statix.spoofax.TigerScope;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@TigerScope
public class TigerComplete implements TaskDef<TigerComplete.Input, @Nullable CompletionResult> {

    public static class Input implements Serializable {
        public final ResourceKey resourceKey;
        public final int caretLocation;
        public final Supplier<@Nullable IStrategoTerm> astSupplier;
        public final Function<IStrategoTerm, @Nullable String> prettyPrinterFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> preAnalyzeFunction;
        public final Function<IStrategoTerm, @Nullable IStrategoTerm> postAnalyzeFunction;

        public Input(
            ResourceKey resourceKey,
            int caretLocation,
            Supplier<IStrategoTerm> astSupplier,
            Function<IStrategoTerm, @Nullable String> prettyPrinterFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> preAnalyzeFunction,
            Function<IStrategoTerm, @Nullable IStrategoTerm> postAnalyzeFunction
        ) {
            this.resourceKey = resourceKey;
            this.caretLocation = caretLocation;
            this.astSupplier = astSupplier;
            this.prettyPrinterFunction = prettyPrinterFunction;
            this.preAnalyzeFunction = preAnalyzeFunction;
            this.postAnalyzeFunction = postAnalyzeFunction;
        }
    }

    private final Logger log;
    private final TermCompleter completer = new TermCompleter();
    private final StrategoTerms strategoTerms;
    private final ITermFactory termFactory;
    private final Provider<StatixAnalyzer> analyzerProvider;

    @Inject public TigerComplete(
        LoggerFactory loggerFactory,
        StrategoTerms strategoTerms,
        ITermFactory termFactory,
        Provider<StatixAnalyzer> analyzerProvider
    ) {
        this.log = loggerFactory.create(TigerComplete.class);
        this.strategoTerms = strategoTerms;
        this.termFactory = termFactory;
        this.analyzerProvider = analyzerProvider;
    }

    @Override
    public String getId() {
        return this.getClass().getName();
    }

    @Override
    public @Nullable CompletionResult exec(ExecContext context, Input input) throws Exception {
        StatixAnalyzer analyzer = analyzerProvider.get();

        // 1) Get the file in which code completion is invoked & parse the file with syntactic completions enabled,
        //    resulting in an AST with placeholders
        //    ==> This should be done by specifying the correct astProvider
        // TODO: get the ast in 'completion mode', with placeholders (use placeholder recovery or inference)
        @Nullable IStrategoTerm ast = input.astSupplier.get(context);
        if (ast == null){
            log.error("Completion failed: we didn't get an AST.");
            return null;   // Cannot complete when we don't get an AST.
        }

        @Nullable IStrategoTerm explicatedAst = explicate(context, input, ast);
        if (explicatedAst == null) {
            log.error("Completion failed: we did not get an explicated AST.");
            return null;    // Cannot complete when we don't get an explicated AST.
        }

        // Convert to Statix AST
        IStrategoTerm annotatedAst = StrategoTermIndices.index(explicatedAst, input.resourceKey.toString(), termFactory);
        ITerm tmpStatixAst = strategoTerms.fromStratego(annotatedAst);
        PlaceholderVarMap placeholderVarMap = new PlaceholderVarMap(input.resourceKey.toString());
        ITerm statixAst = StrategoPlaceholders.replacePlaceholdersByVariables(tmpStatixAst, placeholderVarMap);
        @Nullable ITermVar placeholderVar = findPlaceholderAt(statixAst, input.caretLocation);
        if (placeholderVar == null) {
            log.error("Completion failed: we don't know the placeholder.");
            return null;   // Cannot complete when we don't know the placeholder.
        }

        // 4) Get the solver state of the program (whole project),
        //    which should have some remaining constraints on the placeholder.
        //    TODO: What to do when the file is semantically incorrect? Recovery?
        SolverContext ctx = analyzer.createContext(placeholderVar);
        // TODO: Specify spec name and root rule name somewhere
        SolverState startState = analyzer.createStartState(statixAst, "static-semantics", "programOk")
            .withExistentials(placeholderVarMap.getVars());
        SolverState initialState = analyzer.analyze(ctx, startState);
        if (initialState.hasErrors()) {
            log.error("Completion failed: input program validation failed.\n" + initialState.toString());
            return null;    // Cannot complete when analysis fails.
        }
        if (initialState.getConstraints().isEmpty()) {
            log.error("Completion failed: no constraints left, nothing to complete.\n" + initialState.toString());
            return null;    // Cannot complete when there are no constraints left.
        }

        // 5) Invoke the completer on the solver state, indicating the placeholder for which we want completions
        // 6) Get the possible completions back, as a list of ASTs with new solver states
        List<IStrategoTerm> completionTerms = complete(ctx, initialState, placeholderVar, placeholderVarMap);

        // 7) Format each completion as a proposal, with pretty-printed text
        List<String> completionStrings = completionTerms.stream().map(t -> {
            try {
                @Nullable IStrategoTerm deexplicatedTerm = deexplicate(context, input, t);
                if (deexplicatedTerm == null) return t.toString();  // Return the term when deexplication failed
                @Nullable String prettyPrinted = prettyPrint(context, deexplicatedTerm, input.prettyPrinterFunction);
                return prettyPrinted != null ? prettyPrinted : deexplicatedTerm.toString();    // Return the term when pretty-printing failed
            } catch(ExecException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        // 8) Insert the selected completion: insert the pretty-printed text in the code,
        //    and (maybe?) add the solver state to the current solver state
        List<CompletionItem> completionItems = completionStrings.stream().map(s -> createCompletionProposal(s, input.caretLocation)).collect(Collectors.toList());

        if (completionItems.isEmpty()) {
            log.warn("Completion returned no completion proposals.");
        }

        return new CompletionResult(ListView.copyOf(completionItems), Objects.requireNonNull(getRegion(placeholderVar)), true);
    }

    /**
     * Creates a completion proposal.
     *
     * @param text the text to insert
     * @param caretOffset the caret location
     * @return the created proposal
     */
    private CompletionItem createCompletionProposal(String text, int caretOffset) {
        ListView<TextEdit> textEdits = ListView.of(new TextEdit(Region.atOffset(caretOffset), text));
        String label = normalizeText(text);
        StyleName style = Objects.requireNonNull(StyleName.fromString("meta.template"));
        return new CompletionItem(label, "", "", "", "", style, textEdits, false);
    }

    private String normalizeText(String text) {
        // Replace all sequences of layout with a single space
        return text.replaceAll("\\s+", " ");
    }

    /**
     * Returns the pretty-printed version of the specified term.
     *
     * @param context the execution context
     * @param term the term to pretty-print
     * @param prettyPrinterFunction the pretty-printer function
     * @return the pretty-printed term; or {@code null} when it failed
     */
    private @Nullable String prettyPrint(ExecContext context, IStrategoTerm term, Function<IStrategoTerm, String> prettyPrinterFunction) throws ExecException, InterruptedException {
        return prettyPrinterFunction.apply(context, term);
    }

    private List<IStrategoTerm> complete(SolverContext ctx, SolverState state, ITermVar placeholderVar, PlaceholderVarMap placeholderVarMap) throws InterruptedException {
        List<TermCompleter.CompletionSolverProposal> proposalTerms = completer.complete(ctx, state, placeholderVar);
        return proposalTerms.stream().map(p -> {
            ITerm replacedTerms = StrategoPlaceholders.replaceVariablesByPlaceholders(p.getTerm(), placeholderVarMap);
            return strategoTerms.toStratego(replacedTerms);
        }).collect(Collectors.toList());
    }

    /**
     * Finds the placeholder near the caret location in the specified term.
     *
     * This method assumes all terms in the term are uniquely identifiable,
     * for example through a term index or unique tree path.
     *
     * @param term the term (an AST with placeholders)
     * @param caretOffset the caret location
     * @return the placeholder; or {@code null} if not found
     */
    private @Nullable ITermVar findPlaceholderAt(ITerm term, int caretOffset) {
        if (!termContainsCaret(term, caretOffset)) return null;
        // Recurse into the term
        return term.match(Terms.cases(
            (appl) -> appl.getArgs().stream().map(a -> findPlaceholderAt(a, caretOffset)).filter(Objects::nonNull).findFirst().orElse(null),
            (list) -> list.match(ListTerms.cases(
                (cons) -> {
                    @Nullable final ITermVar headMatch = findPlaceholderAt(cons.getHead(), caretOffset);
                    if (headMatch != null) return headMatch;
                    return findPlaceholderAt(cons.getTail(), caretOffset);
                },
                (nil) -> null,
                (var) -> null
            )),
            (string) -> null,
            (integer) -> null,
            (blob) -> null,
            (var) -> isPlaceholder(var) ? var : null
        ));
    }

    private boolean isPlaceholder(ITermVar var) {
        return TermPlaceholder.has(var);
    }

    /**
     * Determines whether the specified term contains the specified caret offset.
     *
     * @param term the term
     * @param caretOffset the caret offset to find
     * @return {@code true} when the term contains the caret offset;
     * otherwise, {@code false}.
     */
    private boolean termContainsCaret(ITerm term, int caretOffset) {
        @Nullable Region region = getRegion(term);
        if (region == null) {
            // One of the children must contain the caret
            return term.match(Terms.cases(
                (appl) -> appl.getArgs().stream().anyMatch(a -> termContainsCaret(a, caretOffset)),
                (list) -> list.match(ListTerms.cases(
                    (cons) -> {
                        final boolean headContains = termContainsCaret(cons.getHead(), caretOffset);
                        if (headContains) return true;
                        return termContainsCaret(cons.getTail(), caretOffset);
                    },
                    (nil) -> false,
                    (var) -> false
                )),
                (string) -> false,
                (integer) -> false,
                (blob) -> false,
                (var) -> false
            ));
        }
        return region.contains(caretOffset);
    }

    /**
     * Gets the region occupied by the specified term.
     *
     * @param term the term
     * @return the term's region; or {@code null} when it could not be determined
     */
    private static @Nullable Region getRegion(ITerm term) {
        @Nullable final TermOrigin origin = TermOrigin.get(term).orElse(null);
        if (origin == null) return null;
        final ImploderAttachment imploderAttachment = origin.getImploderAttachment();
        // We get the zero-based offset of the first character in the token
        int startOffset = imploderAttachment.getLeftToken().getStartOffset();
        // We get the zero-based offset of the character following the token, which is why we have to add 1
        int endOffset = imploderAttachment.getRightToken().getEndOffset() + 1;
        // If the token is empty or malformed, we skip it. (An empty token cannot contain a caret anyway.)
        if (endOffset <= startOffset) return null;

        return Region.fromOffsets(
            startOffset,
            endOffset
        );
    }

    private @Nullable IStrategoTerm explicate(ExecContext context, Input input, IStrategoTerm term) throws ExecException, InterruptedException {
        return input.preAnalyzeFunction.apply(context, term);
    }

    private @Nullable IStrategoTerm deexplicate(ExecContext context, Input input, IStrategoTerm term) throws ExecException, InterruptedException {
        return input.postAnalyzeFunction.apply(context, term);
    }
}
