package mb.tiger.statix.spoofax;

import dagger.Module;
import dagger.Provides;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderOriginTermFactory;
import org.spoofax.terms.TermFactory;

@Module
public class SpoofaxModule {

    @Provides @TigerScope
    ITermFactory provideTermFactory() {
        return new ImploderOriginTermFactory(new TermFactory());
    }

}
