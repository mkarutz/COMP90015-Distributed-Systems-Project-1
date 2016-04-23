package activitystreamer.core.commandprocessor;

import activitystreamer.core.command.ICommand;
import activitystreamer.server.Connection;

public class ServerCommandProcessor implements ICommandProcessor {
    @Override
    public void processCommand(Connection connection, ICommand command) {
        System.out.println(command);
    }
}
