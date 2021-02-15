package mb.tiger.statix.eclipse;

import dagger.Component;
import mb.spoofax.core.platform.PlatformComponent;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.eclipse.EclipseLanguageComponent;
import mb.spoofax.eclipse.EclipsePlatformComponent;
import mb.tiger.statix.spoofax.SpoofaxModule;
import mb.tiger.statix.spoofax.TigerResourcesComponent;

@mb.tiger.statix.spoofax.TigerScope
@Component(
    modules = {
        mb.tiger.statix.spoofax.TigerModule.class,
        mb.tiger.statix.eclipse.TigerEclipseModule.class,
        SpoofaxModule.class
    },
    dependencies = {
        TigerResourcesComponent.class,
        ResourceServiceComponent.class,
        EclipsePlatformComponent.class
    }
)
public interface TigerEclipseComponent extends EclipseLanguageComponent, mb.tiger.statix.spoofax.TigerComponent {
    mb.tiger.statix.eclipse.TigerEditorTracker getEditorTracker();
}
