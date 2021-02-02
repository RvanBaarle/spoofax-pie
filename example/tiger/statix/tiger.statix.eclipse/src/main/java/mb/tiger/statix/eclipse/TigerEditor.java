package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.editor.SpoofaxEditor;

public class TigerEditor extends SpoofaxEditor {
    public TigerEditor() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
