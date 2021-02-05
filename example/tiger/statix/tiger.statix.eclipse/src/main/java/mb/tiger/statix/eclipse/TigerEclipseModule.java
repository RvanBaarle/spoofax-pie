package mb.tiger.statix.eclipse;

import dagger.Module;
import dagger.Provides;
import mb.pie.api.Pie;
import mb.spoofax.core.language.LanguageInstance;
import mb.spoofax.eclipse.EclipseIdentifiers;
import mb.spoofax.eclipse.job.LockRule;
import mb.spoofax.eclipse.job.ReadLockRule;

import mb.tiger.statix.spoofax.TigerScope;

import javax.inject.Named;

@Module
public class TigerEclipseModule {
    @Provides @TigerScope
    static EclipseIdentifiers provideEclipseIdentifiers() {
        return new mb.tiger.statix.eclipse.TigerEclipseIdentifiers();
    }

    @Provides @Named("StartupWriteLock") @TigerScope
    static LockRule provideStartupWriteLockRule() {
        return new LockRule("Startup write lock");
    }

    @Provides @TigerScope
    static ReadLockRule provideStartupReadLockRule(@Named("StartupWriteLock") LockRule writeLock) {
        return new ReadLockRule(writeLock, "Startup read lock");
    }
}
