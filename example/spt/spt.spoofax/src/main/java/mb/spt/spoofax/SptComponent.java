package mb.spt.spoofax;

import dagger.Component;
import mb.spoofax.core.language.LanguageScope;
import mb.spoofax.core.platform.PlatformComponent;
import mb.spt.spoofax.SptModule;
import mb.spt.spoofax.task.SptDesugar;
import mb.spt.spoofax.task.SptParse;

@LanguageScope
@Component(modules = {SptModule.class}, dependencies = PlatformComponent.class)
public interface SptComponent extends GeneratedSptComponent {
    SptParse getSptParse();
    SptDesugar getSptDesugar();
}
