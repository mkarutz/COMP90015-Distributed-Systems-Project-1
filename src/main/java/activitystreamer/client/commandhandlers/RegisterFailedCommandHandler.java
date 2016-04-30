package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;

public class RegisterFailedCommandHandler implements ICommandHandler {

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof RegisterFailedCommand) {
            RegisterFailedCommand cmd = (RegisterFailedCommand) command;
            conn.close();
            return true;
        } else {
            return false;
        }
    }
}
