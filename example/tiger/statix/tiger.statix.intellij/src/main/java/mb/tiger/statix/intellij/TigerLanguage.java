package mb.tiger.statix.intellij;

import mb.spoofax.intellij.IntellijLanguage;

import javax.inject.Inject;

@mb.tiger.statix.spoofax.TigerScope
public class TigerLanguage extends IntellijLanguage {
    @Inject public TigerLanguage() {
        super(TigerPlugin.getComponent());
    }
}
