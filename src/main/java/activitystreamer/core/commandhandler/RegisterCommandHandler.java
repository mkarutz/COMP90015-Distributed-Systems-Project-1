package activitystreamer.core.commandhandler;

import activitystreamer.core.command.RegisterCommand;
import activitystreamer.server.Connection;

public class RegisterCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            registerCommand = (RegisterCommand)command;
            lockRequestCommand = new LockRequestCommand(registerCommand.getUsername(), registerCommand.getSecret());
            /* TODO: Broadcast lock request and register request with controller to wait async */
        } else {
            return false;
        }
    }
}
