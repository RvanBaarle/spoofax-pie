package mb.tiger.statix.spoofax.task;

import mb.common.message.Messages;
import mb.common.message.MessagesBuilder;
import mb.common.message.Severity;
import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.pie.api.ResourceStringSupplier;
import mb.pie.api.TaskDef;
import mb.resource.ResourceKey;
import javax.inject.Inject;
import java.io.IOException;

@mb.tiger.statix.spoofax.TigerScope
public class TigerCheck implements TaskDef<ResourceKey, Messages> {

    mb.tiger.statix.spoofax.task.TigerParse parse;
    mb.tiger.statix.spoofax.task.TigerAnalyze analyze;

    @Inject public TigerCheck(
    mb.tiger.statix.spoofax.task.TigerParse parse,
    mb.tiger.statix.spoofax.task.TigerAnalyze analyze
    ) {
        this.parse = parse;
        this.analyze = analyze;
    }

    @Override public String getId() {
        return "mb.tiger.statix.spoofax.task.TigerCheck";
    }

    @Override public Messages exec(ExecContext context, ResourceKey key) throws IOException {
        final MessagesBuilder messagesBuilder = new MessagesBuilder();
        final ResourceStringSupplier stringSupplier = new ResourceStringSupplier(key);
        final Messages parseMessages = context.require(parse.createMessagesSupplier(stringSupplier));
        messagesBuilder.addMessages(parseMessages);
        final Result<mb.tiger.statix.spoofax.task.TigerAnalyze.Output, ?> analysisResult = context.require(analyze, new mb.tiger.statix.spoofax.task.TigerAnalyze.Input(key, parse.createRecoverableAstSupplier(stringSupplier)));
        analysisResult
            .ifOk(output -> messagesBuilder.addMessages(output.result.messages))
            .ifErr(e -> messagesBuilder.addMessage("Analysis failed", e, Severity.Error));
        return messagesBuilder.build();
    }
}
