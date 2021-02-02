package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.nature.AddNatureHandler;

public class TigerAddNatureHandler extends AddNatureHandler {
    public TigerAddNatureHandler() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
