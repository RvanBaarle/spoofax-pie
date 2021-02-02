package mb.tiger.statix.intellij;

import mb.spoofax.intellij.SpoofaxPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TigerPlugin {
    private static mb.tiger.statix.intellij.@Nullable TigerIntellijComponent component;

    public static mb.tiger.statix.intellij.TigerIntellijComponent getComponent() {
        if(component == null) {
            throw new RuntimeException("Cannot access TigerIntellijComponent; TigerPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    public static void init() {
        component = DaggerTigerIntellijComponent
            .builder()
            .spoofaxIntellijComponent(SpoofaxPlugin.getComponent())
            .build();
    }
}
