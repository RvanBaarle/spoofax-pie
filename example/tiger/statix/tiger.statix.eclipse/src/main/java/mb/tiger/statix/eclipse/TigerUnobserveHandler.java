package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.menu.UnobserveHandler;

public class TigerUnobserveHandler extends UnobserveHandler {
    public TigerUnobserveHandler() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
