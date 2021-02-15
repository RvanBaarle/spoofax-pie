package mb.tiger.statix.intellij;

import dagger.Component;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.IntellijPlatformComponent;
import mb.tiger.statix.spoofax.TigerModule;
import mb.tiger.statix.intellij.TigerIntellijModule;
import mb.tiger.statix.spoofax.SpoofaxModule;
import mb.tiger.statix.spoofax.TigerResourcesComponent;
import mb.tiger.statix.spoofax.TigerScope;

@TigerScope
@Component(modules = {
    TigerModule.class,
    TigerIntellijModule.class,
    SpoofaxModule.class
}, dependencies = {
    TigerResourcesComponent.class,
    ResourceServiceComponent.class,
    IntellijPlatformComponent.class
})
public interface TigerIntellijComponent extends IntellijLanguageComponent, mb.tiger.statix.spoofax.TigerComponent {
    @Override mb.tiger.statix.intellij.TigerLanguage getLanguage();

    @Override mb.tiger.statix.intellij.TigerFileType getFileType();

    @Override mb.tiger.statix.intellij.TigerFileElementType getFileElementType();
}
