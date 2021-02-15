package mb.tiger.statix.intellij;

import mb.spoofax.intellij.SpoofaxPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import mb.spoofax.core.platform.DaggerResourceServiceComponent;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.intellij.SpoofaxPlugin;
import mb.tiger.statix.spoofax.DaggerTigerResourcesComponent;
import mb.tiger.statix.spoofax.TigerModule;
import mb.tiger.statix.spoofax.TigerResourcesComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TigerPlugin {
    private static @Nullable TigerResourcesComponent resourcesComponent;
    private static @Nullable TigerIntellijComponent component;

    public static TigerIntellijComponent getComponent() {
        if(component == null) {
            throw new RuntimeException("Cannot access TigerIntellijComponent; TigerPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    public static void init() {
        resourcesComponent = DaggerTigerResourcesComponent.create();
        final ResourceServiceComponent resourceServiceComponent = DaggerResourceServiceComponent.builder()
            .resourceServiceModule(SpoofaxPlugin.getResourceServiceComponent().createChildModule().addRegistriesFrom(resourcesComponent))
            .build();
        component = DaggerTigerIntellijComponent.builder()
            .tigerModule(new TigerModule())
            .tigerResourcesComponent(resourcesComponent)
            .resourceServiceComponent(resourceServiceComponent)
            .intellijPlatformComponent(SpoofaxPlugin.getPlatformComponent())
            .build();
    }
}
