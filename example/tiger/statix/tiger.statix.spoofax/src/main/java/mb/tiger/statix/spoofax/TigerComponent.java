package mb.tiger.statix.spoofax;

import dagger.Component;
import mb.spoofax.core.language.LanguageComponent;
import mb.spoofax.core.platform.PlatformComponent;
import mb.resource.ResourceService;
import mb.resource.classloader.ClassLoaderResource;
import mb.pie.api.Pie;
import javax.inject.Provider;

import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.tiger.statix.spoofax.TigerQualifier;

@TigerScope
@Component(
    modules = {
        TigerModule.class,
        TigerModuleExt.class,
        SpoofaxModule.class
    },
    dependencies = {
        TigerResourcesComponent.class,
        ResourceServiceComponent.class,
        PlatformComponent.class
    }
)
public interface TigerComponent extends GeneratedTigerComponent  {
    mb.tiger.statix.spoofax.task.TigerComplete getTigerComplete();
    mb.tiger.statix.spoofax.task.TigerAnalyze getTigerAnalyze();
}
