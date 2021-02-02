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
public class TigerCompileDirectoryCommand implements CommandDef<mb.tiger.statix.spoofax.task.TigerCompileDirectory.Args> {
    private final mb.tiger.statix.spoofax.task.TigerCompileDirectory tigerCompileDirectory;


    @Inject public TigerCompileDirectoryCommand(mb.tiger.statix.spoofax.task.TigerCompileDirectory tigerCompileDirectory) {
        this.tigerCompileDirectory = tigerCompileDirectory;
    }


    @Override public String getId() {
        return tigerCompileDirectory.getId();
    }

    @Override public String getDisplayName() {
        return "Compile directory (list definition names)";
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
            Param.of("dir", mb.resource.hierarchical.ResourcePath.class, true, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.Directory)))
        );
    }

    @Override public mb.tiger.statix.spoofax.task.TigerCompileDirectory.Args fromRawArgs(RawArgs rawArgs) {
        final mb.resource.hierarchical.ResourcePath dir = rawArgs.getOrThrow("dir");
        return new mb.tiger.statix.spoofax.task.TigerCompileDirectory.Args(dir);
    }

    @Override public Task<CommandFeedback> createTask(mb.tiger.statix.spoofax.task.TigerCompileDirectory.Args args) {
        return tigerCompileDirectory.createTask(args);
    }
}
