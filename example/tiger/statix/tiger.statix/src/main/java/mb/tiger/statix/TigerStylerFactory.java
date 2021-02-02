package mb.tiger.statix;

import mb.log.api.LoggerFactory;
import mb.resource.hierarchical.HierarchicalResource;
import mb.spoofax.compiler.interfaces.spoofaxcore.StylerFactory;

public class TigerStylerFactory implements StylerFactory {
    private final mb.tiger.statix.TigerStylingRules stylingRules;
    private final LoggerFactory loggerFactory;

    public TigerStylerFactory(LoggerFactory loggerFactory, HierarchicalResource definitionDir) {
        this.stylingRules = mb.tiger.statix.TigerStylingRules.fromDefinitionDir(definitionDir);
        this.loggerFactory = loggerFactory;
    }

    @Override public mb.tiger.statix.TigerStyler create() {
        return new mb.tiger.statix.TigerStyler(stylingRules, loggerFactory);
    }
}
