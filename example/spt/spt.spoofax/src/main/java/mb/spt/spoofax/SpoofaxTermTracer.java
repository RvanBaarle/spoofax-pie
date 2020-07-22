package mb.spt.spoofax;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Spoofax implementation of {@link ITermTracer};
 */
public final class SpoofaxTermTracer implements ITermTracer {

    @Override
    public @Nullable Region trace(IStrategoTerm term) {
        // TODO:
        return null;
    }

}