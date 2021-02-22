package mb.tiger.statix.intellij;

import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighterFactory;

public class TigerSyntaxHighlighterFactory extends SpoofaxSyntaxHighlighterFactory {
    // Instantiated by IntelliJ.
    private TigerSyntaxHighlighterFactory() {
        super(TigerPlugin.getComponent());
    }
}
