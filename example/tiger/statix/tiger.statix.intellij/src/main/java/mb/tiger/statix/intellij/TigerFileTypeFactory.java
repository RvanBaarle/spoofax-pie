package mb.tiger.statix.intellij;

import mb.spoofax.intellij.IntellijFileTypeFactory;

public class TigerFileTypeFactory extends IntellijFileTypeFactory {
    // Instantiated by IntelliJ.
    private TigerFileTypeFactory() {
        super(mb.tiger.statix.intellij.TigerPlugin.getComponent());
    }
}
