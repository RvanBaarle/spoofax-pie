package mb.tiger.statix.spoofax;

import dagger.Component;
import mb.log.dagger.LoggerComponent;
import mb.resource.dagger.ResourceServiceComponent;
import mb.spoofax.core.platform.PlatformComponent;

@TigerScope
@Component(
    modules = {
        TigerModule.class,
        TigerModuleExt.class,
        SpoofaxModule.class
    },
    dependencies = {
        LoggerComponent.class,
        TigerResourcesComponent.class,
        ResourceServiceComponent.class,
        PlatformComponent.class
    }
)
public interface TigerComponent extends GeneratedTigerComponent  {
    mb.tiger.statix.spoofax.task.TigerComplete getTigerComplete();
    mb.tiger.statix.spoofax.task.TigerAnalyze getTigerAnalyze();
}
