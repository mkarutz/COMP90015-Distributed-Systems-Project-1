package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(ClientReflectionService rClientRefService) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
        //handlers.add(new LoginFailedCommandHandler());
        //handlers.add(new LoginSuccessCommandHandler());
        //handlers.add(new RedirectCommandHandler());
    }
}
