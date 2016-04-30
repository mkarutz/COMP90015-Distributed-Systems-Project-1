package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;

public class LoginFailedCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LoginSuccessCommand) {
            conn.close();
            return true;
        }
        return false;
    }
}
