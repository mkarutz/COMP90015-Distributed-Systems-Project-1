package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.*;
import activitystreamer.util.Settings;

import activitystreamer.client.ClientSolution;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(ClientReflectionService rClientRefService, ClientSolution clientSolution) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
        handlers.add(new LoginSuccessCommandHandler());
        handlers.add(new LoginFailedCommandHandler());
        handlers.add(new RedirectCommandHandler(clientSolution));
        handlers.add(new RegisterSuccessCommandHandler());
        handlers.add(new RegisterFailedCommandHandler());
    }
}
