package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class LoginFailedCommandHandler implements ICommandHandler {

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LoginFailedCommand) {
            conn.close();

            // PRINT_SOMETHING login failed

            return true;
        } else {
            return false;
        }
    }
}
