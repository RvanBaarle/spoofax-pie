package mb.tiger.statix.intellij;

import mb.spoofax.intellij.IntellijFileElementType;

import javax.inject.Inject;

@mb.tiger.statix.spoofax.TigerScope
public class TigerFileElementType extends IntellijFileElementType {
    @Inject public TigerFileElementType() {
        super(mb.tiger.statix.intellij.TigerPlugin.getComponent());
    }
}
