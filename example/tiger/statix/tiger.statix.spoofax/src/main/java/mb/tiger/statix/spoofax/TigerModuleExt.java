package mb.tiger.statix.spoofax;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import mb.log.api.LoggerFactory;
import mb.pie.api.TaskDef;
import mb.statix.common.StatixAnalyzer;
import mb.tiger.statix.TigerAnalyzer;
import mb.tiger.statix.TigerAnalyzerFactory;
import mb.tiger.statix.spoofax.task.TigerDowngradePlaceholders;
import mb.tiger.statix.spoofax.task.TigerIsInj;
import mb.tiger.statix.spoofax.task.TigerPostAnalyze;
import mb.tiger.statix.spoofax.task.TigerPreAnalyze;
import mb.tiger.statix.spoofax.task.TigerPrettyPrint;
import mb.tiger.statix.spoofax.task.TigerStatixSpec;
import mb.tiger.statix.spoofax.task.TigerUpgradePlaceholders;
import org.spoofax.interpreter.terms.ITermFactory;

import java.util.HashSet;
import java.util.Set;

@Module
public class TigerModuleExt {

    @Provides @TigerScope
    static TigerAnalyzerFactory provideAnalyzerFactory(LoggerFactory loggerFactory, ITermFactory termFactory) {
        return new TigerAnalyzerFactory(termFactory, loggerFactory);
    }
    @Provides @TigerScope
    static TigerAnalyzer provideAnalyzer(TigerAnalyzerFactory analyzerFactory) {
        return analyzerFactory.create();
    }
    @Provides @TigerScope
    static StatixAnalyzer bindAnalyzer(TigerAnalyzer analyzer) { return analyzer; }

    @Provides @TigerScope @TigerQualifier @ElementsIntoSet
    static Set<TaskDef<?, ?>> provideTaskDefsSet(
        mb.tiger.statix.spoofax.task.TigerComplete tigerComplete,
        TigerStatixSpec statixSpec,
        TigerPrettyPrint prettyPrintTaskDef,
        TigerPreAnalyze preAnalyzeTaskDef,
        TigerPostAnalyze postAnalyzeTaskDef,
        TigerIsInj isInjTaskDef,
        TigerUpgradePlaceholders upgradePlaceholdersTaskDef,
        TigerDowngradePlaceholders downgradePlaceholdersTaskDef
    ) {
        final HashSet<TaskDef<?, ?>> taskDefs = new HashSet<>();
        taskDefs.add(tigerComplete);
        taskDefs.add(statixSpec);
        taskDefs.add(prettyPrintTaskDef);
        taskDefs.add(preAnalyzeTaskDef);
        taskDefs.add(postAnalyzeTaskDef);
        taskDefs.add(isInjTaskDef);
        taskDefs.add(upgradePlaceholdersTaskDef);
        taskDefs.add(downgradePlaceholdersTaskDef);
        return taskDefs;
    }

}
