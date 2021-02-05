package mb.tiger.statix.spoofax;

import dagger.Module;
import dagger.Provides;
//import mb.nabl2.terms.stratego.StrategoTerms;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;

@Module
public class SpoofaxModule {

    @Provides @TigerScope
    ITermFactory provideTermFactory() {
        return new ImploderOriginTermFactory(new TermFactory());
    }
//
//    @Provides @TigerScope
//    StrategoTerms provideStrategoTerms(ITermFactory termFactory) {
//        return new StrategoTerms(termFactory);
//    }

}
