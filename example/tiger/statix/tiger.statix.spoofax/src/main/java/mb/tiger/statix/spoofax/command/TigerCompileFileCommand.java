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
public class TigerCompileFileCommand implements CommandDef<mb.tiger.statix.spoofax.task.TigerCompileFile.Args> {
    private final mb.tiger.statix.spoofax.task.TigerCompileFile tigerCompileFile;


    @Inject public TigerCompileFileCommand(mb.tiger.statix.spoofax.task.TigerCompileFile tigerCompileFile) {
        this.tigerCompileFile = tigerCompileFile;
    }


    @Override public String getId() {
        return tigerCompileFile.getId();
    }

    @Override public String getDisplayName() {
        return "Compile file (list literals)";
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
            Param.of("file", mb.resource.hierarchical.ResourcePath.class, true, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.File)))
        );
    }

    @Override public mb.tiger.statix.spoofax.task.TigerCompileFile.Args fromRawArgs(RawArgs rawArgs) {
        final mb.resource.hierarchical.ResourcePath file = rawArgs.getOrThrow("file");
        return new mb.tiger.statix.spoofax.task.TigerCompileFile.Args(file);
    }

    @Override public Task<CommandFeedback> createTask(mb.tiger.statix.spoofax.task.TigerCompileFile.Args args) {
        return tigerCompileFile.createTask(args);
    }
}
