package mb.tiger.statix.eclipse;

import mb.spoofax.eclipse.command.RunCommandHandler;

public class TigerRunCommandHandler extends RunCommandHandler {
    public TigerRunCommandHandler() {
        super(mb.tiger.statix.eclipse.TigerPlugin.getComponent());
    }
}
