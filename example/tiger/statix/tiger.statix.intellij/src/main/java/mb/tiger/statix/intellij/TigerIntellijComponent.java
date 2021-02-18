package mb.tiger.statix.intellij;

import dagger.Component;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.intellij.IntellijLanguageComponent;
import mb.spoofax.intellij.IntellijPlatformComponent;

@mb.tiger.statix.spoofax.TigerScope
@Component(
    modules = {
        mb.tiger.statix.spoofax.TigerModule.class,
        mb.tiger.statix.intellij.TigerIntellijModule.class, 
        mb.tiger.statix.spoofax.SpoofaxModule.class,

        mb.tiger.statix.spoofax.TigerModuleExt.class
    },
    dependencies = {
        mb.tiger.statix.spoofax.TigerResourcesComponent.class,
        ResourceServiceComponent.class,
        IntellijPlatformComponent.class
    }
)
public interface TigerIntellijComponent extends IntellijLanguageComponent, mb.tiger.statix.spoofax.TigerComponent {
    @Override mb.tiger.statix.intellij.TigerLanguage getLanguage();

    @Override mb.tiger.statix.intellij.TigerFileType getFileType();

    @Override mb.tiger.statix.intellij.TigerFileElementType getFileElementType();
}
