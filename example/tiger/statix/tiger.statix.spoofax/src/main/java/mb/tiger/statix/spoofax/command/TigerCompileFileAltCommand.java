package mb.tiger.statix.spoofax.command;

import mb.common.util.EnumSetView;
import mb.common.util.ListView;
import mb.pie.api.Task;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.command.CommandContextType;
import mb.spoofax.core.language.command.CommandDef;
import mb.spoofax.core.language.command.CommandExecutionType;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spoofax.core.language.command.arg.ArgProvider;
import mb.spoofax.core.language.command.arg.Param;
import mb.spoofax.core.language.command.arg.ParamDef;
import mb.spoofax.core.language.command.arg.RawArgs;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.inject.Inject;

@mb.tiger.statix.spoofax.TigerScope
public class TigerCompileFileAltCommand implements CommandDef<mb.tiger.statix.spoofax.task.TigerCompileFileAlt.Args> {
    private final mb.tiger.statix.spoofax.task.TigerCompileFileAlt tigerCompileFileAlt;


    @Inject public TigerCompileFileAltCommand(mb.tiger.statix.spoofax.task.TigerCompileFileAlt tigerCompileFileAlt) {
        this.tigerCompileFileAlt = tigerCompileFileAlt;
    }


    @Override public String getId() {
        return tigerCompileFileAlt.getId();
    }

    @Override public String getDisplayName() {
        return "Alternative compile file";
    }

    @Override public String getDescription() {
        return "";
    }

    @Override public EnumSetView<CommandExecutionType> getSupportedExecutionTypes() {
        return EnumSetView.of(
            CommandExecutionType.ManualOnce,
            CommandExecutionType.ManualContinuous,
            CommandExecutionType.AutomaticContinuous
        );
    }

    @Override public ParamDef getParamDef() {
        return new ParamDef(
            Param.of("file", mb.resource.hierarchical.ResourcePath.class, true, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.File))),
            Param.of("listDefNames", boolean.class, false, ListView.of(ArgProvider.value(true))),
            Param.of("base64Encode", boolean.class, false, ListView.of(ArgProvider.value(false))),
            Param.of("compiledFileNameSuffix", java.lang.String.class, false, ListView.of(ArgProvider.value("defnames.aterm")))
        );
    }

    @Override public mb.tiger.statix.spoofax.task.TigerCompileFileAlt.Args fromRawArgs(RawArgs rawArgs) {
        final mb.resource.hierarchical.ResourcePath file = rawArgs.getOrThrow("file");
        final @Nullable boolean listDefNames = rawArgs.getOrNull("listDefNames");
        final @Nullable boolean base64Encode = rawArgs.getOrNull("base64Encode");
        final java.lang.@Nullable String compiledFileNameSuffix = rawArgs.getOrNull("compiledFileNameSuffix");
        return new mb.tiger.statix.spoofax.task.TigerCompileFileAlt.Args(file, listDefNames, base64Encode, compiledFileNameSuffix);
    }

    @Override public Task<CommandFeedback> createTask(mb.tiger.statix.spoofax.task.TigerCompileFileAlt.Args args) {
        return tigerCompileFileAlt.createTask(args);
    }
}
