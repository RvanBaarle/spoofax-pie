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
//import mb.tiger.statix.spoofax.task.TigerComplete;
import mb.tiger.statix.spoofax.task.TigerPostAnalyze;
import mb.tiger.statix.spoofax.task.TigerPreAnalyze;
import mb.tiger.statix.spoofax.task.TigerPrettyPrint;
//import mb.tiger.statix.spoofax.task.TigerStatixSpec;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Set;


public class TigerInstance implements LanguageInstance {
    private final static SetView<String> fileExtensions = SetView.of("tig");

    private final mb.tiger.statix.spoofax.task.TigerParse tigerParse;
    private final mb.tiger.statix.spoofax.task.TigerTokenize tigerTokenize;
    private final mb.tiger.statix.spoofax.task.TigerCheckMulti tigerCheckMulti;
    private final mb.tiger.statix.spoofax.task.TigerStyle tigerStyle;
//    private final mb.tiger.statix.spoofax.task.TigerComplete tigerComplete;
//    private final TigerStatixSpec statixSpecTaskDef;
    private final TigerPrettyPrint prettyPrintTaskDef;
    private final TigerPreAnalyze preAnalyzeTaskDef;
    private final TigerPostAnalyze postAnalyzeTaskDef;
    private final mb.tiger.statix.spoofax.command.TigerCompileFileCommand tigerCompileFileCommand;
    private final mb.tiger.statix.spoofax.command.TigerCompileFileAltCommand tigerCompileFileAltCommand;
    private final mb.tiger.statix.spoofax.command.TigerCompileDirectoryCommand tigerCompileDirectoryCommand;
    private final mb.tiger.statix.spoofax.command.TigerShowParsedAstCommand tigerShowParsedAstCommand;
    private final mb.tiger.statix.spoofax.command.TigerShowDesugaredAstCommand tigerShowDesugaredAstCommand;
    private final mb.tiger.statix.spoofax.command.TigerShowAnalyzedAstCommand tigerShowAnalyzedAstCommand;
    private final mb.tiger.statix.spoofax.command.TigerShowPrettyPrintedTextCommand tigerShowPrettyPrintedTextCommand;
    private final mb.tiger.statix.spoofax.command.TigerShowScopeGraphCommand tigerShowScopeGraphCommand;
    private final mb.spoofax.core.language.taskdef.NullCompleteTaskDef nullCompleteTaskDef;

    private final CollectionView<CommandDef<?>> commandDefs;
    private final CollectionView<AutoCommandRequest<?>> autoCommandDefs;

    @Inject public TigerInstance(
        mb.tiger.statix.spoofax.task.TigerParse tigerParse,
        mb.tiger.statix.spoofax.task.TigerTokenize tigerTokenize,
        mb.tiger.statix.spoofax.task.TigerCheckMulti tigerCheckMulti,
        mb.tiger.statix.spoofax.task.TigerStyle tigerStyle,
//        mb.tiger.statix.spoofax.task.TigerComplete tigerComplete,
//        TigerStatixSpec statixSpecTaskDef,
        mb.spoofax.core.language.taskdef.NullCompleteTaskDef nullCompleteTaskDef,
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
        this.tigerParse = tigerParse;
        this.tigerTokenize = tigerTokenize;
        this.tigerCheckMulti = tigerCheckMulti;
        this.tigerStyle = tigerStyle;
        this.nullCompleteTaskDef = nullCompleteTaskDef;
//        this.tigerComplete = tigerComplete;
//        this.statixSpecTaskDef = statixSpecTaskDef;
        this.prettyPrintTaskDef = prettyPrintTaskDef;
        this.preAnalyzeTaskDef = preAnalyzeTaskDef;
        this.postAnalyzeTaskDef = postAnalyzeTaskDef;
        this.tigerCompileFileCommand = tigerCompileFileCommand;
        this.tigerCompileFileAltCommand = tigerCompileFileAltCommand;
        this.tigerCompileDirectoryCommand = tigerCompileDirectoryCommand;
        this.tigerShowParsedAstCommand = tigerShowParsedAstCommand;
        this.tigerShowDesugaredAstCommand = tigerShowDesugaredAstCommand;
        this.tigerShowAnalyzedAstCommand = tigerShowAnalyzedAstCommand;
        this.tigerShowPrettyPrintedTextCommand = tigerShowPrettyPrintedTextCommand;
        this.tigerShowScopeGraphCommand = tigerShowScopeGraphCommand;
        this.commandDefs = CollectionView.copyOf(commandDefs);
        this.autoCommandDefs = CollectionView.copyOf(autoCommandDefs);
    }


    @Override public String getDisplayName() {
        return "Tiger";
    }

    @Override public SetView<String> getFileExtensions() {
        return fileExtensions;
    }


    @Override public Task<? extends Option<? extends Tokens<?>>> createTokenizeTask(ResourceKey resourceKey) {
        return tigerTokenize.createTask(resourceKey);
    }

    @Override public Task<Option<Styling>> createStyleTask(ResourceKey resourceKey) {
        return tigerStyle.createTask(tigerParse.createRecoverableTokensSupplier(resourceKey).map(Result::ok));
    }

    @Override
    public Task<@Nullable CompletionResult> createCompletionTask(ResourceKey resourceKey, Region primarySelection) {
        return nullCompleteTaskDef.createTask(new mb.spoofax.core.language.taskdef.NullCompleteTaskDef.Input((ctx) -> mb.pie.api.None.instance)); // TODO: use Result
//        return tigerComplete.createTask(new TigerComplete.Input(
//            resourceKey,
//            primarySelection.getStartOffset(),
//            tigerParse.createAstSupplier(resourceKey).map(Result::get),
//            (c, t) -> prettyPrintTaskDef.createFunction().apply(c, new TigerPrettyPrint.Input(c2 -> t)),
//            (c, t) -> preAnalyzeTaskDef.createFunction().apply(c, new TigerPreAnalyze.Input(c2 -> t)),
//            (c, t) -> postAnalyzeTaskDef.createFunction().apply(c, new TigerPostAnalyze.Input(c2 -> t))
//        ));
    }

    @Override public Task<KeyedMessages> createCheckTask(ResourcePath projectRoot) {
        return tigerCheckMulti.createTask(new mb.tiger.statix.spoofax.task.TigerCheckMulti.Input(
            projectRoot,
            new PathResourceWalker(new NoHiddenPathMatcher()),
            new PathResourceMatcher(new ExtensionsPathMatcher(getFileExtensions().asUnmodifiable()))));
    }

    @Override public CollectionView<CommandDef<?>> getCommandDefs() {
        return commandDefs;
    }

    @Override public CollectionView<AutoCommandRequest<?>> getAutoCommandRequests() {
        return autoCommandDefs;
    }


    @Override public CliCommand getCliCommand() {
        return CliCommand.of(
            "Tiger",
            null,
            null,
            ListView.of(
            ),
            ListView.of(
            )
        )
            ;
    }


    @Override public ListView<MenuItem> getMainMenuItems() {
        return ListView.of(
            MenuItem.menu("Compile",
                MenuItem.menu("Static Semantics",
                    CommandAction.builder().with(
                        tigerCompileFileCommand,
                        CommandExecutionType.ManualOnce,
                        "Compile file (list literals)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - default",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - list literal values instead",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - base64 encode",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - list literal values instead + base64 encode",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - default (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - list literal values instead (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - base64 encode (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - list literal values instead + base64 encode (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )

            )
            ,
            MenuItem.menu("Debug",
                MenuItem.menu("Syntax",
                    CommandAction.builder().with(
                        tigerShowParsedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show parsed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowParsedAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show parsed AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowPrettyPrintedTextCommand,
                        CommandExecutionType.ManualOnce,
                        "Show pretty-printed text",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowPrettyPrintedTextCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show pretty-printed text (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Static Semantics",
                    CommandAction.builder().with(
                        tigerShowAnalyzedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show analyzed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowAnalyzedAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show analyzed AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Transformations",
                    CommandAction.builder().with(
                        tigerShowDesugaredAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show desugared AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowDesugaredAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show desugared AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )

            )

        );
    }

    @Override public ListView<MenuItem> getResourceContextMenuItems() {
        return ListView.of(
            MenuItem.menu("Compile",
                CommandAction.builder().with(
                    tigerCompileFileCommand,
                    CommandExecutionType.ManualOnce,
                    "Compile file (list literals)",
                    new RawArgs(MapView.<String, Serializable>of())
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileDirectoryCommand,
                    CommandExecutionType.ManualOnce,
                    "Compile directory (list definition names)",
                    new RawArgs(MapView.<String, Serializable>of())
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes()
                    .addRequiredResourceTypes(HierarchicalResourceType.Directory)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualOnce,
                    "Alternative compile file - default",
                    new RawArgs(MapView.<String, Serializable>of())
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualOnce,
                    "Alternative compile file - list literal values instead",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualOnce,
                    "Alternative compile file - base64 encode",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualOnce,
                    "Alternative compile file - list literal values instead + base64 encode",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualContinuous,
                    "Alternative compile file - default (continuous)",
                    new RawArgs(MapView.<String, Serializable>of())
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualContinuous,
                    "Alternative compile file - list literal values instead (continuous)",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualContinuous,
                    "Alternative compile file - base64 encode (continuous)",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()
                ,
                CommandAction.builder().with(
                    tigerCompileFileAltCommand,
                    CommandExecutionType.ManualContinuous,
                    "Alternative compile file - list literal values instead + base64 encode (continuous)",
                    new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                )
                    .addRequiredEditorSelectionTypes()
                    .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                    .addRequiredResourceTypes(HierarchicalResourceType.File)
                    .addRequiredEnclosingResourceTypes()
                    .buildItem()

            )
            ,
            MenuItem.menu("Debug",
                MenuItem.menu("Syntax",
                    CommandAction.builder().with(
                        tigerShowParsedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show parsed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowPrettyPrintedTextCommand,
                        CommandExecutionType.ManualOnce,
                        "Show pretty-printed text",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Static Semantics",
                    CommandAction.builder().with(
                        tigerShowAnalyzedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show analyzed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Transformations",
                    CommandAction.builder().with(
                        tigerShowDesugaredAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show desugared AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )

            )

        );
    }

    @Override public ListView<MenuItem> getEditorContextMenuItems() {
        return ListView.of(
            MenuItem.menu("Compile",
                MenuItem.menu("Static Semantics",
                    CommandAction.builder().with(
                        tigerCompileFileCommand,
                        CommandExecutionType.ManualOnce,
                        "Compile file (list literals)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - default",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - list literal values instead",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - base64 encode",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualOnce,
                        "Alternative compile file - list literal values instead + base64 encode",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - default (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - list literal values instead (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals.aterm")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - base64 encode (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "defnames_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerCompileFileAltCommand,
                        CommandExecutionType.ManualContinuous,
                        "Alternative compile file - list literal values instead + base64 encode (continuous)",
                        new RawArgs(MapView.<String, Serializable>of(new EntryView<String, Serializable>("listDefNames", false), new EntryView<String, Serializable>("base64Encode", true), new EntryView<String, Serializable>("compiledFileNameSuffix", "litvals_base64.txt")))
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes(EditorFileType.HierarchicalResource)
                        .addRequiredResourceTypes(HierarchicalResourceType.File)
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )

            )
            ,
            MenuItem.menu("Debug",
                MenuItem.menu("Syntax",
                    CommandAction.builder().with(
                        tigerShowParsedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show parsed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowParsedAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show parsed AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowPrettyPrintedTextCommand,
                        CommandExecutionType.ManualOnce,
                        "Show pretty-printed text",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowPrettyPrintedTextCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show pretty-printed text (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Static Semantics",
                    CommandAction.builder().with(
                        tigerShowAnalyzedAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show analyzed AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowAnalyzedAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show analyzed AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )
                ,
                MenuItem.menu("Transformations",
                    CommandAction.builder().with(
                        tigerShowDesugaredAstCommand,
                        CommandExecutionType.ManualOnce,
                        "Show desugared AST",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()
                    ,
                    CommandAction.builder().with(
                        tigerShowDesugaredAstCommand,
                        CommandExecutionType.ManualContinuous,
                        "Show desugared AST (continuous)",
                        new RawArgs(MapView.<String, Serializable>of())
                    )
                        .addRequiredEditorSelectionTypes()
                        .addRequiredEditorFileTypes()
                        .addRequiredResourceTypes()
                        .addRequiredEnclosingResourceTypes()
                        .buildItem()

                )

            )

        );
    }
}
