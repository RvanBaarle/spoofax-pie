package mb.sdf3.spoofax;

import dagger.Component;
import mb.sdf3.spoofax.GeneratedSdf3Component;
import mb.sdf3.spoofax.task.Sdf3Parse;
import mb.spoofax.core.language.LanguageScope;
import mb.spoofax.core.platform.PlatformComponent;

@LanguageScope
@Component(modules = {Sdf3Module.class}, dependencies = PlatformComponent.class)
public interface Sdf3Component extends GeneratedSdf3Component {

    Sdf3Parse getParse();

}
