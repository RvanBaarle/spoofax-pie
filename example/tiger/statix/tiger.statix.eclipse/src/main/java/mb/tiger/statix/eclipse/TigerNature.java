package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.nature.SpoofaxNature;

public class TigerNature extends SpoofaxNature {
    public TigerNature() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
