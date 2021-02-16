package mb.tiger.statix.spoofax;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import mb.log.api.LoggerFactory;
import mb.pie.api.MapTaskDefs;
import mb.pie.api.Pie;
import mb.pie.api.PieBuilder;
import mb.pie.api.TaskDef;
import mb.pie.api.TaskDefs;
import mb.pie.api.serde.JavaSerde;
import mb.resource.ResourceService;
import mb.resource.hierarchical.HierarchicalResource;
import mb.spoofax.core.language.LanguageInstance;
import mb.spoofax.core.language.command.AutoCommandRequest;
import mb.spoofax.core.language.command.CommandDef;
import mb.spoofax.core.platform.Platform;
import mb.statix.common.StatixAnalyzer;
import mb.stratego.common.StrategoRuntime;
import mb.stratego.common.StrategoRuntimeBuilder;
import mb.tiger.statix.TigerAnalyzer;
import mb.tiger.statix.TigerAnalyzerFactory;
import mb.tiger.statix.TigerClassLoaderResources;
import mb.tiger.statix.spoofax.task.TigerPostAnalyze;
import mb.tiger.statix.spoofax.task.TigerPreAnalyze;
import mb.tiger.statix.spoofax.task.TigerPrettyPrint;
import mb.tiger.statix.spoofax.task.TigerStatixSpec;
import org.spoofax.interpreter.terms.ITermFactory;

import javax.inject.Named;
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

    @Provides @TigerScope @ElementsIntoSet
    static Set<TaskDef<?, ?>> provideTaskDefsSet(
        mb.tiger.statix.spoofax.task.TigerComplete tigerComplete,
        TigerStatixSpec statixSpec,
        TigerPrettyPrint prettyPrintTaskDef,
        TigerPreAnalyze preAnalyzeTaskDef,
        TigerPostAnalyze postAnalyzeTaskDef
    ) {
        final HashSet<TaskDef<?, ?>> taskDefs = new HashSet<>();
        taskDefs.add(tigerComplete);
        taskDefs.add(statixSpec);
        taskDefs.add(prettyPrintTaskDef);
        taskDefs.add(preAnalyzeTaskDef);
        taskDefs.add(postAnalyzeTaskDef);
        return taskDefs;
    }

}
