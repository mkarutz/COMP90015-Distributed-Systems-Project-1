package activitystreamer.core.commandhandler;

import activitystreamer.core.command.ActivityMessageCommand;
import activitystreamer.server.Connection;

public class ActivityMessageCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command) {
        if (command is ActivityMessageCommand) {

            /* TODO: */
        } else {
            return false;
        }
    }
}
