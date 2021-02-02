package mb.tiger.statix;

import mb.common.style.Styling;
import mb.common.token.Token;
import mb.esv.common.ESVStyler;
import mb.log.api.LoggerFactory;
import mb.spoofax.compiler.interfaces.spoofaxcore.Styler;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class TigerStyler implements Styler {
    private final ESVStyler styler;

    public TigerStyler(mb.tiger.statix.TigerStylingRules stylingRules, LoggerFactory loggerFactory) {
        this.styler = new ESVStyler(stylingRules.stylingRules, loggerFactory);
    }

    public Styling style(Iterable<? extends Token<IStrategoTerm>> tokens) {
        return styler.style(tokens);
    }
}
