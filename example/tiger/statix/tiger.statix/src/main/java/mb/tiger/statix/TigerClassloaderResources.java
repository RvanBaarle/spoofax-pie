package mb.tiger.statix;

import mb.resource.ResourceKeyString;
import mb.resource.classloader.ClassLoaderResource;
import mb.resource.classloader.ClassLoaderResourceRegistry;

public class TigerClassloaderResources {
    public static ClassLoaderResourceRegistry createClassLoaderResourceRegistry() {
        return new ClassLoaderResourceRegistry("mb-tiger-statix-classloader-resource", TigerClassloaderResources.class.getClassLoader());
    }

    public static ClassLoaderResource createDefinitionDir(ClassLoaderResourceRegistry classLoaderResourceRegistry) {
        return classLoaderResourceRegistry.getResource(ResourceKeyString.of("mb/tiger/statix"));
    }
}
