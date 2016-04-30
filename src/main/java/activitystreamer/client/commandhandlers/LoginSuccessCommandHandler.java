package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;

public class LoginSuccessCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(Command command, Connection conn) {
        return command instanceof LoginSuccessCommand;
    }
}
