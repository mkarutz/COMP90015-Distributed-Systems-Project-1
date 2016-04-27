package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;

public class RegisterSuccessCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof RegisterSuccessCommand) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        return false;
    }
}
