package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class RegisterFailedCommandHandler implements ICommandHandler {

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        return false;
    }
}
