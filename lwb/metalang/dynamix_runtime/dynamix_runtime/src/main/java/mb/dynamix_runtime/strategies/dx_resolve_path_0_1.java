package mb.dynamix_runtime.strategies;

import com.google.common.collect.ImmutableList;
import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.stratego.StrategoAnnotations;
import mb.nabl2.terms.stratego.StrategoTerms;
import mb.statix.scopegraph.Scope;
import mb.statix.solver.IState;
import mb.statix.solver.persistent.SolverResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static mb.nabl2.terms.build.TermBuild.B;
import static mb.nabl2.terms.matching.TermMatch.M;

public class dx_resolve_path_0_1 extends Strategy {

    public static dx_resolve_path_0_1 instance = new dx_resolve_path_0_1();

    @Override
    public @Nullable IStrategoTerm invoke(Context context, IStrategoTerm scurrent, IStrategoTerm sterm) {
        ITermFactory factory = context.getFactory();
        StrategoTerms strategoTerms = new StrategoTerms(factory);
        ITerm analysisTerm = strategoTerms.fromStratego(sterm);
        ITerm scopeTerm = strategoTerms.fromStratego(scurrent);

        final @Nullable SolverResult analysis = M.blobValue(SolverResult.class).match(analysisTerm)
            .orElse(null);
        if(analysis == null) return null;
        final IState.Immutable state = analysis.state();
        return M.cases(
            M.term(Scope.matcher(), (t, s) -> {
                Set<ITerm> labels = state.scopeGraph().getLabels();
                return B.newList(labels.stream().flatMap(l ->
                            StreamSupport.stream(state.scopeGraph().getEdges(s, l).spliterator(), false)
                                .filter(s2 -> {
                                    final @Nullable StrategoAnnotations annos = s2.getAttachments().get(StrategoAnnotations.class);
                                    if(annos == null) return false;
                                    final ImmutableList<IStrategoTerm> annoList = annos.getAnnotationList();
                                    if(annoList.size() == 1) {
                                        return annoList.get(0).match(factory.makeAppl("OfSort", factory.makeAppl("SCOPE")));
                                    }
                                    return false;
                                })
                        )
                        .collect(Collectors.toList())
                );
            })
        ).match(scopeTerm).map(strategoTerms::toStratego).orElse(null);
    }
}
