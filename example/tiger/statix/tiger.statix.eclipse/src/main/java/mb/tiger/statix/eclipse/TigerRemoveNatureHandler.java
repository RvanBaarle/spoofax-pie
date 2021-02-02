package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.nature.RemoveNatureHandler;

public class TigerRemoveNatureHandler extends RemoveNatureHandler {
    public TigerRemoveNatureHandler() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
