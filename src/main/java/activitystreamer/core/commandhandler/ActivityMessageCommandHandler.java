package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.server.Connection;

public class ActivityMessageCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command,Connection conn) {
        if (command instanceof ActivityMessageCommand) {

          return true;
            /* TODO: */
        } else {
            return false;
        }
    }
}
