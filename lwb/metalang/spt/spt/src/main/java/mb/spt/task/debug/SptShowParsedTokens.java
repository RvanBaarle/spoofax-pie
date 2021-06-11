package mb.spt.task.debug;

import mb.jsglr.pie.ShowParsedTokensTaskDef;
import mb.pie.api.ExecContext;
import mb.pie.api.stamp.resource.ResourceStampers;
import mb.spoofax.core.language.command.CommandFeedback;
import mb.spt.SptClassLoaderResources;
import mb.spt.SptScope;
import mb.spt.task.SptParse;

import javax.inject.Inject;

@SptScope
public class SptShowParsedTokens extends ShowParsedTokensTaskDef {
    private final SptClassLoaderResources classLoaderResources;

    @Inject public SptShowParsedTokens(SptParse parse, SptClassLoaderResources classLoaderResources) {
        super(parse);
        this.classLoaderResources = classLoaderResources;
    }

    @Override public String getId() {
        return getClass().getName();
    }

    @Override public CommandFeedback exec(ExecContext context, Args args) throws Exception {
        context.require(classLoaderResources.tryGetAsLocalResource(getClass()), ResourceStampers.hashFile());
        return super.exec(context, args);
    }
}
