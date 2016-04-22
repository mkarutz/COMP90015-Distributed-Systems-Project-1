package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.server.Connection;
import activitystreamer.util.Settings;

public class AuthenticateCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            AuthenticateCommand authCommand = (AuthenticateCommand)command;
            if (Settings.getSecret().equals(authCommand.getSecret())) {
                /* Incoming server connection authenticated */
                /* CHANGE STATE */
            } else {
                ICommand authFailCommand = new AuthenticationFailCommand("Secret was incorrect.");
                conn.pushCommand(authFailCommand);
            }

            return true;
        } else {
            return false;
        }
    }
}
