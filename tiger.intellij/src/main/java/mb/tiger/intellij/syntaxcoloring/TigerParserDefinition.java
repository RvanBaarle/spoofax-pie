package mb.tiger.intellij.syntaxcoloring;

import mb.spoofax.intellij.editor.SpoofaxParserDefinition;
import mb.tiger.intellij.TigerPlugin;

public class TigerParserDefinition extends SpoofaxParserDefinition {
    public TigerParserDefinition() {
        super(
            TigerPlugin.getComponent().getFileType(),
            TigerPlugin.getComponent().getFileElementType(),
            TigerPlugin.getComponent().getTokenTypeManager()
        );
    }
}