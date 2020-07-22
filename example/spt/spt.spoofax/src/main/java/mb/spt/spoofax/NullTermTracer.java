package mb.spt.spoofax;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Implementation of {@link ITermTracer} that always returns {@code null}.
 */
public final class NullTermTracer implements ITermTracer {

    @Override
    public @Nullable Region trace(IStrategoTerm term) {
        return null;
    }

}
