package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;

public class RegisterCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        return false; /* Clients don't receive register commands */
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
