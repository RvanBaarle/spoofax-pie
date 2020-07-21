package mb.spt.spoofax;

import mb.common.region.Region;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface ITermTracer {

    /**
     * Determines the source region of the specified term.
     *
     * @param term the term to trace
     * @return the source region; or {@code null} when it could not be determined
     */
    @Nullable Region trace(IStrategoTerm term);

}
