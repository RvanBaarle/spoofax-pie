package mb.tiger.statix.spoofax;

import mb.common.message.KeyedMessages;
import mb.common.option.Option;
import mb.common.result.Result;
import mb.common.style.Styling;
import mb.common.region.Region;
import mb.common.token.Tokens;
import mb.common.util.CollectionView;
import mb.common.util.EntryView;
import mb.common.util.ListView;
import mb.common.util.MapView;
import mb.common.util.SetView;
import mb.completions.common.CompletionResult;
import mb.pie.api.Task;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.resource.hierarchical.match.PathResourceMatcher;
import mb.resource.hierarchical.match.path.ExtensionsPathMatcher;
import mb.resource.hierarchical.match.path.NoHiddenPathMatcher;
import mb.resource.hierarchical.walk.PathResourceWalker;
import mb.spoofax.core.language.LanguageInstance;
import mb.spoofax.core.language.cli.CliCommand;
import mb.spoofax.core.language.command.AutoCommandRequest;
import mb.spoofax.core.language.command.CommandDef;
import mb.spoofax.core.language.command.CommandExecutionType;
import mb.spoofax.core.language.command.EditorFileType;
import mb.spoofax.core.language.command.HierarchicalResourceType;
import mb.spoofax.core.language.command.arg.RawArgs;
import mb.spoofax.core.language.menu.CommandAction;
import mb.spoofax.core.language.menu.MenuItem;
import mb.tiger.statix.spoofax.task.TigerComplete;
import mb.tiger.statix.spoofax.task.TigerPostAnalyze;
import mb.tiger.statix.spoofax.task.TigerPreAnalyze;
import mb.tiger.statix.spoofax.task.TigerPrettyPrint;
import mb.tiger.statix.spoofax.task.TigerStatixSpec;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;


public class TigerInstance extends GeneratedTigerInstance {
    private final mb.tiger.statix.spoofax.task.TigerParse tigerParse;
    private final mb.tiger.statix.spoofax.task.TigerComplete tigerComplete;
    private final TigerStatixSpec statixSpecTaskDef;
    private final TigerPrettyPrint prettyPrintTaskDef;
    private final TigerPreAnalyze preAnalyzeTaskDef;
    private final TigerPostAnalyze postAnalyzeTaskDef;

    @Inject public TigerInstance(
        mb.tiger.statix.spoofax.task.TigerParse tigerParse,
        mb.tiger.statix.spoofax.task.TigerTokenize tigerTokenize,
        mb.tiger.statix.spoofax.task.TigerCheckMulti tigerCheckMulti,
        mb.tiger.statix.spoofax.task.TigerStyle tigerStyle,
        mb.tiger.statix.spoofax.task.TigerComplete tigerComplete,
        TigerStatixSpec statixSpecTaskDef,
        TigerPrettyPrint prettyPrintTaskDef,
        TigerPreAnalyze preAnalyzeTaskDef,
        TigerPostAnalyze postAnalyzeTaskDef,
        mb.tiger.statix.spoofax.command.TigerCompileFileCommand tigerCompileFileCommand,
        mb.tiger.statix.spoofax.command.TigerCompileFileAltCommand tigerCompileFileAltCommand,
        mb.tiger.statix.spoofax.command.TigerCompileDirectoryCommand tigerCompileDirectoryCommand,
        mb.tiger.statix.spoofax.command.TigerShowParsedAstCommand tigerShowParsedAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowDesugaredAstCommand tigerShowDesugaredAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowAnalyzedAstCommand tigerShowAnalyzedAstCommand,
        mb.tiger.statix.spoofax.command.TigerShowPrettyPrintedTextCommand tigerShowPrettyPrintedTextCommand,
        mb.tiger.statix.spoofax.command.TigerShowScopeGraphCommand tigerShowScopeGraphCommand,
        Set<CommandDef<?>> commandDefs,
        Set<AutoCommandRequest<?>> autoCommandDefs
    ) {
        super(
            tigerParse,
            tigerTokenize,
            tigerCheckMulti,
            tigerStyle,
            tigerComplete,
            tigerCompileFileCommand,
            tigerCompileFileAltCommand,
            tigerCompileDirectoryCommand,
            tigerShowParsedAstCommand,
            tigerShowDesugaredAstCommand,
            tigerShowAnalyzedAstCommand,
            tigerShowPrettyPrintedTextCommand,
            tigerShowScopeGraphCommand,
            commandDefs,
            autoCommandDefs
        );
        this.tigerParse = tigerParse;
        this.tigerComplete = tigerComplete;
        this.statixSpecTaskDef = statixSpecTaskDef;
        this.prettyPrintTaskDef = prettyPrintTaskDef;
        this.preAnalyzeTaskDef = preAnalyzeTaskDef;
        this.postAnalyzeTaskDef = postAnalyzeTaskDef;
    }


    @Override
    public Task<@Nullable CompletionResult> createCompletionTask(ResourceKey resourceKey, Region primarySelection) {
        return tigerComplete.createTask(new TigerComplete.Input(
            resourceKey,
            primarySelection.getStartOffset(),
            tigerParse.createAstSupplier(resourceKey).map(Result::get),
            (c, t) -> prettyPrintTaskDef.createFunction().apply(c, new TigerPrettyPrint.Input(c2 -> t)),
            (c, t) -> preAnalyzeTaskDef.createFunction().apply(c, new TigerPreAnalyze.Input(c2 -> t)),
            (c, t) -> postAnalyzeTaskDef.createFunction().apply(c, new TigerPostAnalyze.Input(c2 -> t))
        ));
    }

}
