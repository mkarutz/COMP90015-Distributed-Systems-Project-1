package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.*;
import activitystreamer.util.Settings;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(ClientReflectionService rClientRefService) {
        super();

        incomingHandlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
        incomingHandlers.add(new LoginSuccessCommandHandler());
        incomingHandlers.add(new LoginFailedCommandHandler());
        incomingHandlers.add(new RegisterSuccessCommandHandler());
        incomingHandlers.add(new RegisterFailedCommandHandler());

        outgoingHandlers.add(new LoginCommandHandler());
        outgoingHandlers.add(new LogoutCommandHandler());
        outgoingHandlers.add(new RegisterCommandHandler());
        outgoingHandlers.add(new ActivityMessageCommandHandler());
    }
}
