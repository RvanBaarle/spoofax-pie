package mb.str.incr;

import mb.common.message.KeyedMessages;
import mb.common.message.KeyedMessagesBuilder;
import mb.common.message.Severity;
import mb.common.region.Region;
import mb.resource.ResourceKey;
import mb.resource.ResourceKeyString;
import mb.resource.ResourceRuntimeException;
import mb.resource.ResourceService;
import mb.resource.hierarchical.ResourcePath;
import mb.stratego.build.strincr.message.Message;
import mb.stratego.build.strincr.message.MessageSeverity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.jsglr2.messages.SourceRegion;

public class MessageConverter {
    public static void addMessagesToBuilder(KeyedMessagesBuilder messagesBuilder, Iterable<Message> messages, ResourceService resourceService) {
        for(Message message : messages) {
            final @Nullable SourceRegion sourceRegion = message.sourceRegion;
            final @Nullable Region region;
            if(sourceRegion != null) {
                region = Region.fromOffsets(sourceRegion.startOffset, sourceRegion.endOffset, sourceRegion.startRow, sourceRegion.endRow);
            } else {
                region = null;
            }
            @Nullable ResourceKey resourceKey;
            try {
                resourceKey = resourceService.getResourceKey(ResourceKeyString.parse(message.locationTermString));
            } catch(ResourceRuntimeException e) {
                resourceKey = null;
            }
            final Severity severity = convertSeverity(message.severity);
            messagesBuilder.addMessage(message.getMessage(), severity, resourceKey, region);
        }
    }

    public static KeyedMessages convertMessages(ResourcePath rootDirectory, Iterable<Message> messages, ResourceService resourceService) {
        final KeyedMessagesBuilder messagesBuilder = new KeyedMessagesBuilder();
        addMessagesToBuilder(messagesBuilder, messages, resourceService);
        return messagesBuilder.build(rootDirectory);
    }

    private static Severity convertSeverity(MessageSeverity severity) {
        switch(severity) {
            case NOTE:
                return Severity.Info;
            case WARNING:
                return Severity.Warning;
            case ERROR:
                return Severity.Error;
        }
        return Severity.Error;
    }
}