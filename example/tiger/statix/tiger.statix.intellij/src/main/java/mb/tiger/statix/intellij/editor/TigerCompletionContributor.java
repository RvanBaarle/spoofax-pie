package mb.tiger.statix.intellij.editor;

import mb.spoofax.intellij.editor.SpoofaxCompletionContributor;
import mb.tiger.statix.intellij.TigerPlugin;

public class TigerCompletionContributor extends SpoofaxCompletionContributor {
    // Instantiated by IntelliJ.
    protected TigerCompletionContributor() {
        super(TigerPlugin.getComponent(), TigerPlugin.getPieComponent());
    }
}
