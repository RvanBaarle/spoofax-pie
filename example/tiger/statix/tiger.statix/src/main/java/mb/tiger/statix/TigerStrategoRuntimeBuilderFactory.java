package mb.tiger.statix;

import mb.log.api.LoggerFactory;
import mb.resource.ResourceService;
import mb.resource.hierarchical.HierarchicalResource;
import mb.spoofax.compiler.interfaces.spoofaxcore.StrategoRuntimeBuilderFactory;
import mb.stratego.common.StrategoRuntimeBuilder;

public class TigerStrategoRuntimeBuilderFactory implements StrategoRuntimeBuilderFactory {
    private final LoggerFactory loggerFactory;
    private final ResourceService resourceService;
    private final HierarchicalResource definitionDir;

    public TigerStrategoRuntimeBuilderFactory(LoggerFactory loggerFactory, ResourceService resourceService, HierarchicalResource definitionDir) {
        this.loggerFactory = loggerFactory;
        this.resourceService = resourceService;
        this.definitionDir = definitionDir;
    }

    @Override public StrategoRuntimeBuilder create() {
        final StrategoRuntimeBuilder builder = new StrategoRuntimeBuilder(loggerFactory, resourceService, definitionDir);
        builder.addCtree(definitionDir.appendRelativePath("target/metaborg/stratego.ctree"));
        builder.withJarParentClassLoader(TigerStrategoRuntimeBuilderFactory.class.getClassLoader());
        builder.addLibrary(new mb.nabl2.common.NaBL2PrimitiveLibrary());
        builder.addLibrary(new mb.statix.common.StatixPrimitiveLibrary());
        builder.addLibrary(new mb.spoofax2.common.primitive.Spoofax2PrimitiveLibrary(loggerFactory, resourceService));
        builder.addContextObject(new mb.spoofax2.common.primitive.generic.Spoofax2LanguageContext(
            "org.metaborg",
            "tiger.statix",
            "code-completion-202010-SNAPSHOT",
            definitionDir.getPath()
        ));
        builder.addLibrary(new mb.constraint.common.stratego.ConstraintPrimitiveLibrary(resourceService));
        return builder;
    }
}
