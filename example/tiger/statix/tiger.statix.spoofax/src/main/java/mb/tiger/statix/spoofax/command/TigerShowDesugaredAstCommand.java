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
public class TigerShowDesugaredAstCommand implements CommandDef<mb.tiger.statix.spoofax.task.TigerShowArgs> {
    private final mb.tiger.statix.spoofax.task.TigerShowDesugaredAst tigerShowDesugaredAst;


    @Inject public TigerShowDesugaredAstCommand(mb.tiger.statix.spoofax.task.TigerShowDesugaredAst tigerShowDesugaredAst) {
        this.tigerShowDesugaredAst = tigerShowDesugaredAst;
    }


    @Override public String getId() {
        return tigerShowDesugaredAst.getId();
    }

    @Override public String getDisplayName() {
        return "Show desugared AST";
    }

    @Override public String getDescription() {
        return "Shows the desugared Abstract Syntax Tree of the program.";
    }

    @Override public EnumSetView<CommandExecutionType> getSupportedExecutionTypes() {
        return EnumSetView.of(
            CommandExecutionType.ManualOnce,
            CommandExecutionType.ManualContinuous
        );
    }

    @Override public ParamDef getParamDef() {
        return new ParamDef(
            Param.of("resource", mb.resource.ResourceKey.class, true, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.ResourceKey))),
            Param.of("region", mb.common.region.Region.class, false, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.Region)))
        );
    }

    @Override public mb.tiger.statix.spoofax.task.TigerShowArgs fromRawArgs(RawArgs rawArgs) {
        final mb.resource.ResourceKey resource = rawArgs.getOrThrow("resource");
        final mb.common.region.@Nullable Region region = rawArgs.getOrNull("region");
        return new mb.tiger.statix.spoofax.task.TigerShowArgs(resource, region);
    }

    @Override public Task<CommandFeedback> createTask(mb.tiger.statix.spoofax.task.TigerShowArgs args) {
        return tigerShowDesugaredAst.createTask(args);
    }
}
