package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.*;
import activitystreamer.util.Settings;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(ClientReflectionService rClientRefService) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
        handlers.add(new LoginSuccessCommandHandler());
        handlers.add(new LoginFailedCommandHandler());
        //handlers.add(new RedirectCommandHandler());
        handlers.add(new RegisterSuccessCommandHandler());
        handlers.add(new RegisterFailedCommandHandler());
    }
}
