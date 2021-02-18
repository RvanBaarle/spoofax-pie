package mb.tiger.statix.intellij;

import mb.spoofax.core.platform.DaggerResourceServiceComponent;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.intellij.SpoofaxPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TigerPlugin {
    private static mb.tiger.statix.spoofax.@Nullable TigerResourcesComponent resourcesComponent;
    private static mb.tiger.statix.intellij.@Nullable TigerIntellijComponent component;

    public static mb.tiger.statix.spoofax.TigerResourcesComponent getResourcesComponent() {
        if(resourcesComponent == null) {
            throw new RuntimeException("Cannot access TigerResourcesComponent; TigerPlugin has not been started yet, or has been stopped");
        }
        return resourcesComponent;
    }

    public static mb.tiger.statix.intellij.TigerIntellijComponent getComponent() {
        if(component == null) {
            throw new RuntimeException("Cannot access TigerIntellijComponent; TigerPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    public static void init() {
        resourcesComponent = mb.tiger.statix.spoofax.DaggerTigerResourcesComponent.create();
        final ResourceServiceComponent resourceServiceComponent = DaggerResourceServiceComponent.builder()
            .resourceServiceModule(SpoofaxPlugin.getResourceServiceComponent().createChildModule().addRegistriesFrom(resourcesComponent))
            .build();
        component = DaggerTigerIntellijComponent.builder()
            .tigerResourcesComponent(resourcesComponent)
            .resourceServiceComponent(resourceServiceComponent)
            .intellijPlatformComponent(SpoofaxPlugin.getPlatformComponent())
            .build();
    }
}
