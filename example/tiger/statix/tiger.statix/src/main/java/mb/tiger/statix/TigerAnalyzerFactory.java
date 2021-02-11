package mb.tiger.statix;

import mb.log.api.LoggerFactory;
import mb.statix.common.StatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Inject;

public class TigerAnalyzerFactory {
    private final StatixSpec spec;
    private ITermFactory termFactory;
    private LoggerFactory loggerFactory;

    @Inject public TigerAnalyzerFactory(
        ITermFactory termFactory,
        LoggerFactory loggerFactory
    ) {
        this.termFactory = termFactory;
        this.loggerFactory = loggerFactory;
        this.spec = StatixSpec.fromClassLoaderResources(TigerAnalyzerFactory.class, "/mb/tiger/statix.aterm");
        // TODO: This is a FileSpec(), we need a Spec():
//        this.spec = StatixSpec.fromClassLoaderResources(TigerAnalyzerFactory.class, "/mb/tiger/src-gen/static/static-semantics.spec.aterm");
    }

    public TigerAnalyzer create() {
        return new TigerAnalyzer(spec, termFactory, loggerFactory);
    }
}
