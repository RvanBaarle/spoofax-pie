package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.build.SpoofaxProjectBuilder;

public class TigerProjectBuilder extends SpoofaxProjectBuilder {
    public TigerProjectBuilder() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
