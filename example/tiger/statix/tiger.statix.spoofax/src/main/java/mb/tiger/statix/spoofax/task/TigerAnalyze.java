package mb.tiger.statix.spoofax.task;

import mb.constraint.common.ConstraintAnalyzer.SingleFileResult;
import mb.constraint.common.ConstraintAnalyzerContext;
import mb.constraint.pie.ConstraintAnalyzeTaskDef;
import mb.pie.api.ExecContext;
import mb.pie.api.None;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.HierarchicalResource;
import mb.resource.hierarchical.match.AllResourceMatcher;
import mb.resource.hierarchical.match.FileResourceMatcher;
import mb.resource.hierarchical.match.PathResourceMatcher;
import mb.resource.hierarchical.match.path.ExtensionPathMatcher;
import mb.stratego.common.StrategoRuntime;
import mb.tiger.statix.TigerClassLoaderResources;
import mb.tiger.statix.TigerConstraintAnalyzer;
import mb.tiger.statix.TigerConstraintAnalyzerFactory;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

@mb.tiger.statix.spoofax.TigerScope
public class TigerAnalyze extends ConstraintAnalyzeTaskDef {
    private final TigerClassLoaderResources classLoaderResources;
    private final mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer;
    private final TigerGetStrategoRuntimeProvider getStrategoRuntimeProvider;

    @Inject
    public TigerAnalyze(
        TigerClassLoaderResources classLoaderResources,
        mb.tiger.statix.TigerConstraintAnalyzer constraintAnalyzer,
        TigerGetStrategoRuntimeProvider getStrategoRuntimeProvider
    ) {
        this.classLoaderResources = classLoaderResources;
        this.constraintAnalyzer = constraintAnalyzer;
        this.getStrategoRuntimeProvider = getStrategoRuntimeProvider;
    }

    @Override
    public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerAnalyze";
    }

    @Override
    protected SingleFileResult analyze(ExecContext context, ResourceKey resource, IStrategoTerm ast, ConstraintAnalyzerContext constraintAnalyzerContext) throws Exception {
        try {
            classLoaderResources.performWithDefinitionResourceLocations(
                "src-gen/statix",
                directory -> {
                    try(final Stream<? extends HierarchicalResource> stream = directory.walk(
                        new AllResourceMatcher(new FileResourceMatcher(), new PathResourceMatcher(new ExtensionPathMatcher("aterm")))
                    )) {
                        stream.forEach(statixSpecResource -> {
                            try {
                                context.require(statixSpecResource, ResourceStampers.hashFile());
                            } catch(IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
                    }
                },
                jarFileWithPath -> context.require(jarFileWithPath.file)
            );
        } catch(UncheckedIOException e) {
            throw e.getCause();
        }
        context.require(classLoaderResources.tryGetAsLocalResource(getClass()), ResourceStampers.hashFile());
        context.require(classLoaderResources.tryGetAsLocalResource(TigerConstraintAnalyzer.class), ResourceStampers.hashFile());
        context.require(classLoaderResources.tryGetAsLocalResource(TigerConstraintAnalyzerFactory.class), ResourceStampers.hashFile());
        final StrategoRuntime strategoRuntime = context.require(getStrategoRuntimeProvider, None.instance).getValue().get();
        return constraintAnalyzer.analyze(resource, ast, constraintAnalyzerContext, strategoRuntime);
    }
}
