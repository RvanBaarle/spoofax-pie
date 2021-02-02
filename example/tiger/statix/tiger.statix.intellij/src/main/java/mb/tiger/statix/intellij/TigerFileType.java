package mb.tiger.statix.intellij;

import mb.spoofax.intellij.IntellijLanguageFileType;

import javax.inject.Inject;

@mb.tiger.statix.spoofax.TigerScope
public class TigerFileType extends IntellijLanguageFileType {
    @Inject protected TigerFileType() {
        super(mb.tiger.statix.intellij.TigerPlugin.getComponent());
    }
}
