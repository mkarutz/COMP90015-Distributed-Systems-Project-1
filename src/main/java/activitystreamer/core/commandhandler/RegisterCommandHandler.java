package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.server.Connection;

public class RegisterCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            RegisterCommand registerCommand = (RegisterCommand) command;
            LockRequestCommand lockRequestCommand = new LockRequestCommand(registerCommand.getUsername(), registerCommand.getSecret());

            return true;
            /* TODO: Broadcast lock request and register request with controller to wait async */
        } else {
            return false;
        }
    }
}
