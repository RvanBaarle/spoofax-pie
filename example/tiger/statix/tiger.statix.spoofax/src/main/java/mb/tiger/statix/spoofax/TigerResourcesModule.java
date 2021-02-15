package mb.tiger.statix.spoofax;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import mb.resource.ResourceRegistry;
import mb.resource.classloader.ClassLoaderResource;
import mb.resource.classloader.ClassLoaderResourceRegistry;
import mb.resource.hierarchical.HierarchicalResource;
import mb.tiger.statix.TigerClassLoaderResources;

@Module
public class TigerResourcesModule {
    @Provides @TigerResourcesScope
    static TigerClassLoaderResources provideClassLoaderResources() {
        return new TigerClassLoaderResources();
    }

    @Provides @TigerResourcesScope @TigerQualifier
    static ClassLoaderResourceRegistry provideClassLoaderResourceRegistry(TigerClassLoaderResources classLoaderResources) {
        return classLoaderResources.resourceRegistry;
    }

    @Provides @TigerResourcesScope @TigerQualifier("definition-directory")
    static ClassLoaderResource provideDefinitionDirectory(TigerClassLoaderResources classLoaderResources) {
        return classLoaderResources.definitionDirectory;
    }

    @Provides @TigerResourcesScope @TigerQualifier("definition-directory")
    static HierarchicalResource provideDefinitionDirectoryAsHierarchicalResource(@TigerQualifier("definition-directory") ClassLoaderResource definitionDirectory) {
        return definitionDirectory;
    }

    @Provides @TigerResourcesScope @TigerQualifier @IntoSet
    static ResourceRegistry provideClassLoaderResourceRegistryIntoSet(@TigerQualifier ClassLoaderResourceRegistry registry) {
        return registry;
    }
}
