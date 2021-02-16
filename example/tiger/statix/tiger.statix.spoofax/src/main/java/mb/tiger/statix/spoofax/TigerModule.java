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
import mb.resource.classloader.ClassLoaderResource;
import mb.resource.classloader.ClassLoaderResourceRegistry;
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
public class TigerModule {

    @Provides @TigerScope
    static mb.tiger.statix.TigerParserFactory provideParserFactory(@TigerQualifier("definition-directory") HierarchicalResource definitionDir) {
        return new mb.tiger.statix.TigerParserFactory(definitionDir);
    }

    @Provides /* Unscoped: parser has state, so create a new parser every call. */
    static mb.tiger.statix.TigerParser provideParser(mb.tiger.statix.TigerParserFactory parserFactory) {
        return parserFactory.create();
    }


    @Provides @TigerScope
    static mb.tiger.statix.TigerStylerFactory provideStylerFactory(LoggerFactory loggerFactory, @TigerQualifier("definition-directory") HierarchicalResource definitionDir) {
        return new mb.tiger.statix.TigerStylerFactory(loggerFactory, definitionDir);
    }

    @Provides @TigerScope
    static mb.tiger.statix.TigerStyler provideStyler(mb.tiger.statix.TigerStylerFactory stylerFactory) {
        return stylerFactory.create();
    }

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

    @Provides @TigerScope
    static mb.tiger.statix.TigerConstraintAnalyzerFactory provideConstraintAnalyzerFactory(ResourceService resourceService) {
        return new mb.tiger.statix.TigerConstraintAnalyzerFactory(resourceService);
    }

    @Provides @TigerScope
    static mb.tiger.statix.TigerConstraintAnalyzer provideConstraintAnalyzer(mb.tiger.statix.TigerConstraintAnalyzerFactory factory) {
        return factory.create();
    }


    @Provides @TigerScope
    static mb.tiger.statix.TigerStrategoRuntimeBuilderFactory provideStrategoRuntimeBuilderFactory(LoggerFactory loggerFactory, ResourceService resourceService, @TigerQualifier("definition-directory") HierarchicalResource definitionDir) {
        return new mb.tiger.statix.TigerStrategoRuntimeBuilderFactory(loggerFactory, resourceService, definitionDir);
    }

    @Provides @TigerScope @TigerQualifier
    static StrategoRuntimeBuilder provideQualifiedStrategoRuntimeBuilder(mb.tiger.statix.TigerStrategoRuntimeBuilderFactory factory) {
        return factory.create();
    }

    @Provides @TigerScope
    static StrategoRuntimeBuilder provideStrategoRuntimeBuilder(@TigerQualifier StrategoRuntimeBuilder strategoRuntimeBuilder) {
        return strategoRuntimeBuilder;
    }

    @Provides @TigerScope @Named("prototype")
    static StrategoRuntime providePrototypeStrategoRuntime(StrategoRuntimeBuilder builder) {
        return builder.build();
    }

    @Provides @TigerQualifier /* Unscoped: new stratego runtime every call. */
    static StrategoRuntime provideQualifiedStrategoRuntime(StrategoRuntimeBuilder builder, @Named("prototype") StrategoRuntime prototype) {
        return builder.buildFromPrototype(prototype);
    }

    @Provides /* Unscoped: new stratego runtime every call. */
    static StrategoRuntime provideStrategoRuntime(@TigerQualifier StrategoRuntime strategoRuntime) {
        return strategoRuntime;
    }

    @Provides @TigerScope @ElementsIntoSet
    static Set<TaskDef<?, ?>> provideTaskDefsSet(
        mb.tiger.statix.spoofax.task.TigerCompileFile tigerCompileFile,
        mb.tiger.statix.spoofax.task.TigerCompileFileAlt tigerCompileFileAlt,
        mb.tiger.statix.spoofax.task.TigerCompileDirectory tigerCompileDirectory,
        mb.tiger.statix.spoofax.task.TigerShowParsedAst tigerShowParsedAst,
        mb.tiger.statix.spoofax.task.TigerShowDesugaredAst tigerShowDesugaredAst,
        mb.tiger.statix.spoofax.task.TigerShowAnalyzedAst tigerShowAnalyzedAst,
        mb.tiger.statix.spoofax.task.TigerShowPrettyPrintedText tigerShowPrettyPrintedText,
        mb.tiger.statix.spoofax.task.TigerShowScopeGraph tigerShowScopeGraph,
        mb.tiger.statix.spoofax.task.TigerTokenize tigerTokenize,
        mb.tiger.statix.spoofax.task.TigerParse tigerParse,
        mb.tiger.statix.spoofax.task.TigerStyle tigerStyle,
        mb.tiger.statix.spoofax.task.TigerComplete tigerComplete,
        TigerStatixSpec statixSpec,
        TigerPrettyPrint prettyPrintTaskDef,
        TigerPreAnalyze preAnalyzeTaskDef,
        TigerPostAnalyze postAnalyzeTaskDef,
        mb.tiger.statix.spoofax.task.TigerAnalyze tigerAnalyze,
        mb.tiger.statix.spoofax.task.TigerAnalyzeMulti tigerAnalyzeMulti,
        mb.tiger.statix.spoofax.task.TigerCheck tigerCheck,
        mb.tiger.statix.spoofax.task.TigerCheckMulti tigerCheckMulti,
        mb.tiger.statix.spoofax.task.TigerCheckAggregator tigerCheckAggregator
    ) {
        final HashSet<TaskDef<?, ?>> taskDefs = new HashSet<>();
        taskDefs.add(tigerCompileFile);
        taskDefs.add(tigerCompileFileAlt);
        taskDefs.add(tigerCompileDirectory);
        taskDefs.add(tigerShowParsedAst);
        taskDefs.add(tigerShowDesugaredAst);
        taskDefs.add(tigerShowAnalyzedAst);
        taskDefs.add(tigerShowPrettyPrintedText);
        taskDefs.add(tigerShowScopeGraph);
        taskDefs.add(tigerTokenize);
        taskDefs.add(tigerParse);
        taskDefs.add(tigerStyle);
        taskDefs.add(tigerComplete);
        taskDefs.add(statixSpec);
        taskDefs.add(prettyPrintTaskDef);
        taskDefs.add(preAnalyzeTaskDef);
        taskDefs.add(postAnalyzeTaskDef);
        taskDefs.add(tigerAnalyze);
        taskDefs.add(tigerAnalyzeMulti);
        taskDefs.add(tigerCheck);
        taskDefs.add(tigerCheckMulti);
        taskDefs.add(tigerCheckAggregator);
        return taskDefs;
    }

    @Provides @TigerScope @TigerQualifier
    static TaskDefs provideQualifiedTaskDefs(Set<TaskDef<?, ?>> taskDefs) {
        return new MapTaskDefs(taskDefs);
    }

    @Provides @TigerScope
    static TaskDefs provideTaskDefs(Set<TaskDef<?, ?>> taskDefs) {
        return new MapTaskDefs(taskDefs);
    }

    @Provides @TigerScope @Named("prototype")
    static Pie providePrototypePie(@Platform PieBuilder pieBuilder, TaskDefs taskDefs, ResourceService resourceService) {
        return pieBuilder
            .addTaskDefs(taskDefs)
            .withResourceService(resourceService)
            .withSerdeFactory(loggerFactory -> new JavaSerde(TigerClassLoaderResources.class.getClassLoader()))
            .build();
    }
    @Provides @TigerScope @TigerQualifier
    static Pie provideQualifiedPie(@Named("prototype") Pie languagePie) {
        return languagePie;
    }

    @Provides @TigerScope
    static Pie providePie(@TigerQualifier Pie languagePie) {
        return languagePie;
    }


    @Provides @TigerScope
    static LanguageInstance provideLanguageInstance(mb.tiger.statix.spoofax.TigerInstance instance) {
        return instance;
    }


    @Provides @TigerScope @ElementsIntoSet
    static Set<CommandDef<?>> provideCommandDefsSet(
        mb.tiger.statix.spoofax.command.TigerCompileFileCommand tigerCompileFileCommand,
        mb.tiger.statix.spoofax.command.TigerCompileFileAltCommand tigerCompileFileAltCommand,
        mb.tiger.statix.spoofax.command.TigerCompileDirectoryCommand tigerCompileDirectoryCommand,
        mb.tiger.statix.spoofax.command.TigerShowParsedAstCommand tigerShowParsedAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowDesugaredAstCommand tigerShowDesugaredAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowAnalyzedAstCommand tigerShowAnalyzedAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowPrettyPrintedTextCommand tigerShowPrettyPrintedTextCommand,
        mb.tiger.statix.spoofax.command.TigerShowScopeGraphCommand tigerShowScopeGraphCommand
    ) {
        final HashSet<CommandDef<?>> commandDefs = new HashSet<>();
        commandDefs.add(tigerCompileFileCommand);
        commandDefs.add(tigerCompileFileAltCommand);
        commandDefs.add(tigerCompileDirectoryCommand);
        commandDefs.add(tigerShowParsedAstCommand);
        commandDefs.add(tigerShowDesugaredAstCommand);
        commandDefs.add(tigerShowAnalyzedAstCommand);
        commandDefs.add(tigerShowPrettyPrintedTextCommand);
        commandDefs.add(tigerShowScopeGraphCommand);
        return commandDefs;
    }

    @Provides @TigerScope @ElementsIntoSet
    static Set<AutoCommandRequest<?>> provideAutoCommandRequestsSet(
    ) {
        final HashSet<AutoCommandRequest<?>> autoCommandDefs = new HashSet<>();
        return autoCommandDefs;
    }
}
