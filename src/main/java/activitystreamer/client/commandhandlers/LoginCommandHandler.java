package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;

public class LoginCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        return false; /* Clients don't receive login commands */
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
