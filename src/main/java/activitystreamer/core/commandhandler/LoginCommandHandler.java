package activitystreamer.core.commandhandler;

import activitystreamer.core.command.LoginCommand;
import activitystreamer.server.Connection;

public class LoginCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            loginCommand = (LoginCommand)command;

            /* TODO: Check if user is registered in local register */
        } else {
            return false;
        }
    }
}
