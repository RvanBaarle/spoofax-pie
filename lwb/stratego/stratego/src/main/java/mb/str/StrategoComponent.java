package mb.str;

import dagger.Component;
import mb.spoofax.core.platform.PlatformComponent;
import mb.str.config.StrategoConfigModule;
import mb.str.config.StrategoConfigurator;
import mb.str.incr.StrategoIncrModule;

@StrategoScope
@Component(
    modules = {StrategoModule.class, JavaTasksModule.class, StrategoIncrModule.class, StrategoConfigModule.class},
    dependencies = {PlatformComponent.class}
)
public interface StrategoComponent extends GeneratedStrategoComponent {
    StrategoConfigurator getStrategoConfigurator();
}