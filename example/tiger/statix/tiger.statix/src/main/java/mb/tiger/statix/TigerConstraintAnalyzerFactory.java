package mb.tiger.statix;

import mb.resource.ResourceService;
import mb.spoofax.compiler.interfaces.spoofaxcore.ConstraintAnalyzerFactory;

public class TigerConstraintAnalyzerFactory implements ConstraintAnalyzerFactory {
    private final ResourceService resourceService;

    public TigerConstraintAnalyzerFactory(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override public mb.tiger.statix.TigerConstraintAnalyzer create() {
        return new mb.tiger.statix.TigerConstraintAnalyzer(resourceService);
    }
}
