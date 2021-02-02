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
public class TigerShowScopeGraphCommand implements CommandDef<mb.tiger.statix.spoofax.task.TigerShowArgs> {
    private final mb.tiger.statix.spoofax.task.TigerShowScopeGraph tigerShowScopeGraph;


    @Inject public TigerShowScopeGraphCommand(mb.tiger.statix.spoofax.task.TigerShowScopeGraph tigerShowScopeGraph) {
        this.tigerShowScopeGraph = tigerShowScopeGraph;
    }


    @Override public String getId() {
        return tigerShowScopeGraph.getId();
    }

    @Override public String getDisplayName() {
        return "Show scope graph";
    }

    @Override public String getDescription() {
        return "Shows the scope graph for the program";
    }

    @Override public EnumSetView<CommandExecutionType> getSupportedExecutionTypes() {
        return EnumSetView.of(
            CommandExecutionType.ManualOnce,
            CommandExecutionType.ManualContinuous
        );
    }

    @Override public ParamDef getParamDef() {
        return new ParamDef(
            Param.of("resource", mb.resource.ResourceKey.class, true, ListView.of(ArgProvider.context(mb.spoofax.core.language.command.CommandContextType.ResourceKey)))
        );
    }

    @Override public mb.tiger.statix.spoofax.task.TigerShowArgs fromRawArgs(RawArgs rawArgs) {
        final mb.resource.ResourceKey resource = rawArgs.getOrThrow("resource");
        return new mb.tiger.statix.spoofax.task.TigerShowArgs(resource, null);
    }

    @Override public Task<CommandFeedback> createTask(mb.tiger.statix.spoofax.task.TigerShowArgs args) {
        return tigerShowScopeGraph.createTask(args);
    }
}
