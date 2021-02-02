package mb.tiger.statix.intellij;

import dagger.Component;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.SpoofaxIntellijComponent;
import mb.tiger.statix.spoofax.SpoofaxModule;

@mb.tiger.statix.spoofax.TigerScope
@Component(modules = { mb.tiger.statix.spoofax.TigerModule.class,
    mb.tiger.statix.intellij.TigerIntellijModule.class,
    SpoofaxModule.class
}, dependencies = SpoofaxIntellijComponent.class)
public interface TigerIntellijComponent extends IntellijLanguageComponent, mb.tiger.statix.spoofax.TigerComponent {
    @Override mb.tiger.statix.intellij.TigerLanguage getLanguage();

    @Override mb.tiger.statix.intellij.TigerFileType getFileType();

    @Override mb.tiger.statix.intellij.TigerFileElementType getFileElementType();
}
