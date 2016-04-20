package activitystreamer.core.commandhandler;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.server.Connection;

public class AuthenticateCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command is AuthenticateCommand) {
            authCommand = (AuthenticateCommand)command;
            if (Settings.getSecret().equals(authCommand.getSecret()) {
                /* Incoming server connection authenticated */
                /* CHANGE STATE */
            } else {
                ICommand authFailCommand = new AuthenticationFailCommand("Secret was incorrect.");
                conn.pushCommand(authFailCommand);
            }
        } else {
            return false;
        }
    }
}
