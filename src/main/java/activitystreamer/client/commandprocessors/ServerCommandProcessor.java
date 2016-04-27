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
        //handlers.add(new LoginFailedCommandHandler());
        //handlers.add(new LoginSuccessCommandHandler());
        //handlers.add(new RedirectCommandHandler());

        outgoingHandlers.add(new LoginCommandHandler());
        outgoingHandlers.add(new LogoutCommandHandler());
        outgoingHandlers.add(new ActivityMessageCommandHandler());
    }
}
