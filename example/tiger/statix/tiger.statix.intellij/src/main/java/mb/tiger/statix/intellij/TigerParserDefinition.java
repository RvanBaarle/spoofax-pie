package mb.tiger.statix.intellij;

import mb.spoofax.intellij.editor.SpoofaxParserDefinition;

public class TigerParserDefinition extends SpoofaxParserDefinition {
    // Instantiated by IntelliJ.
    private TigerParserDefinition() {
        super(mb.tiger.statix.intellij.TigerPlugin.getComponent());
    }
}
