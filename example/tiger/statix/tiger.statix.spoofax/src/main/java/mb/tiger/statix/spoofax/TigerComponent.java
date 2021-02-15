package mb.tiger.statix.spoofax;

import dagger.Component;
import mb.spoofax.core.language.LanguageComponent;
import mb.spoofax.core.platform.PlatformComponent;
import mb.resource.ResourceService;
import mb.resource.classloader.ClassLoaderResource;
import mb.pie.api.Pie;
import javax.inject.Provider;

import mb.spoofax.core.platform.ResourceServiceComponent;
import mb.tiger.statix.spoofax.TigerQualifier;

@TigerScope
@Component(
    modules = {
        TigerModule.class,
        SpoofaxModule.class
    },
    dependencies = {
        TigerResourcesComponent.class,
        ResourceServiceComponent.class,
        PlatformComponent.class
    }
)
public interface TigerComponent extends LanguageComponent  {
    @Override mb.tiger.statix.spoofax.TigerInstance getLanguageInstance();

    @TigerQualifier("definition-directory") ClassLoaderResource getDefinitionDir();

    @Override @TigerQualifier Pie getPie();


    Provider<mb.tiger.statix.TigerParser> getParserProvider();


    mb.tiger.statix.TigerStyler getStyler();


    mb.tiger.statix.TigerConstraintAnalyzer getConstraintAnalyzer();


    @TigerQualifier mb.stratego.common.StrategoRuntimeBuilder getStrategoRuntimeBuilder();

    @TigerQualifier Provider<mb.stratego.common.StrategoRuntime> getStrategoRuntimeProvider();


    // Task definitions

    mb.tiger.statix.spoofax.task.TigerCompileFile getTigerCompileFile();
    mb.tiger.statix.spoofax.task.TigerCompileFileAlt getTigerCompileFileAlt();
    mb.tiger.statix.spoofax.task.TigerCompileDirectory getTigerCompileDirectory();
    mb.tiger.statix.spoofax.task.TigerShowParsedAst getTigerShowParsedAst();
    mb.tiger.statix.spoofax.task.TigerShowDesugaredAst getTigerShowDesugaredAst();
    mb.tiger.statix.spoofax.task.TigerShowAnalyzedAst getTigerShowAnalyzedAst();
    mb.tiger.statix.spoofax.task.TigerShowPrettyPrintedText getTigerShowPrettyPrintedText();
    mb.tiger.statix.spoofax.task.TigerShowScopeGraph getTigerShowScopeGraph();
    mb.tiger.statix.spoofax.task.TigerTokenize getTigerTokenize();
    mb.tiger.statix.spoofax.task.TigerParse getTigerParse();
    mb.tiger.statix.spoofax.task.TigerStyle getTigerStyle();
    mb.tiger.statix.spoofax.task.TigerComplete getTigerComplete();
    mb.tiger.statix.spoofax.task.TigerAnalyze getTigerAnalyze();
    mb.tiger.statix.spoofax.task.TigerAnalyzeMulti getTigerAnalyzeMulti();
    mb.tiger.statix.spoofax.task.TigerCheck getTigerCheck();
    mb.tiger.statix.spoofax.task.TigerCheckMulti getTigerCheckMulti();
    mb.tiger.statix.spoofax.task.TigerCheckAggregator getTigerCheckAggregator();


    // Command definitions

    mb.tiger.statix.spoofax.command.TigerCompileFileCommand getTigerCompileFileCommand();
    mb.tiger.statix.spoofax.command.TigerCompileFileAltCommand getTigerCompileFileAltCommand();
    mb.tiger.statix.spoofax.command.TigerCompileDirectoryCommand getTigerCompileDirectoryCommand();
    mb.tiger.statix.spoofax.command.TigerShowParsedAstCommand getTigerShowParsedAstCommand();
    mb.tiger.statix.spoofax.command.TigerShowDesugaredAstCommand getTigerShowDesugaredAstCommand();
    mb.tiger.statix.spoofax.command.TigerShowAnalyzedAstCommand getTigerShowAnalyzedAstCommand();
    mb.tiger.statix.spoofax.command.TigerShowPrettyPrintedTextCommand getTigerShowPrettyPrintedTextCommand();
    mb.tiger.statix.spoofax.command.TigerShowScopeGraphCommand getTigerShowScopeGraphCommand();
}
