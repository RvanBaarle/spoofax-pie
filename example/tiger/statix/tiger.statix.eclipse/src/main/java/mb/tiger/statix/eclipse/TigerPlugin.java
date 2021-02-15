package mb.tiger.statix.eclipse;

import mb.pie.api.ExecException;
import mb.spoofax.core.platform.DaggerResourceServiceComponent;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.eclipse.SpoofaxPlugin;
import mb.spoofax.eclipse.util.StatusUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import mb.spoofax.core.platform.DaggerResourceServiceComponent;
import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.spoofax.eclipse.SpoofaxPlugin;
import mb.spoofax.eclipse.util.StatusUtil;
import mb.tiger.statix.spoofax.DaggerTigerResourcesComponent;
import mb.tiger.statix.spoofax.TigerResourcesComponent;

import java.io.IOException;

public class TigerPlugin extends AbstractUIPlugin {
    public static final String pluginId = "tiger.statix.eclipse";

    private static @Nullable TigerResourcesComponent resourcesComponent;
    private static mb.tiger.statix.eclipse.@Nullable TigerEclipseComponent component;

    public static mb.tiger.statix.eclipse.TigerEclipseComponent getComponent() {
        if(component == null) {
            throw new RuntimeException(
                "Cannot access TigerEclipseComponent; TigerPlugin has not been started yet, or has been stopped");
        }
        return component;
    }

    @Override public void start(@NonNull BundleContext context) throws Exception {
        super.start(context);
        resourcesComponent = DaggerTigerResourcesComponent.create();
        final ResourceServiceComponent resourceServiceComponent = DaggerResourceServiceComponent.builder()
            .resourceServiceModule(SpoofaxPlugin.getResourceServiceComponent().createChildModule().addRegistriesFrom(resourcesComponent))
            .build();
        component = DaggerTigerEclipseComponent.builder()
            .tigerResourcesComponent(resourcesComponent)
            .resourceServiceComponent(resourceServiceComponent)
            .eclipsePlatformComponent(SpoofaxPlugin.getPlatformComponent())
            .build();

        component.getEditorTracker().register();

        WorkspaceJob job = new WorkspaceJob("Tiger startup") {
            @Override public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                try {
                    SpoofaxPlugin.getPlatformComponent().getPieRunner().startup(component, monitor);
                } catch(IOException | ExecException | InterruptedException e) {
                    throw new CoreException(StatusUtil.error("Tiger startup job failed unexpectedly", e));
                }
                return StatusUtil.success();
            }
        };
        job.setRule(component.startupWriteLockRule());
        job.schedule();
    }

    @Override public void stop(@NonNull BundleContext context) throws Exception {
        super.stop(context);
        component = null;
        resourcesComponent = null;
    }
}
