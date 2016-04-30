package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;

import activitystreamer.client.ClientSolution;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(ClientReflectionService rClientRefService, ClientSolution clientSolution) {
        super();

        handlers.add(new InvalidMessageCommandHandler());
        handlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
        handlers.add(new LoginSuccessCommandHandler());
        handlers.add(new LoginFailedCommandHandler());
        handlers.add(new RedirectCommandHandler(clientSolution));
        handlers.add(new RegisterSuccessCommandHandler());
        handlers.add(new RegisterFailedCommandHandler());
    }

    @Override
    public void invalidMessage(Connection connection, Command command) {
        Command invalidCommand = new InvalidMessageCommand("Command type is invalid.");
        connection.pushCommand(invalidCommand);
        connection.close();
    }
}
