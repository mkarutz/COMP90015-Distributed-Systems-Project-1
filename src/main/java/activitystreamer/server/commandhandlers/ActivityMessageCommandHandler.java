package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class ActivityMessageCommandHandler implements ICommandHandler {
    UserAuthService rAuthService;

    public ActivityMessageCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command,Connection conn) {
        if (command instanceof ActivityMessageCommand) {

            // TODO: Check if user is logged in correctly according to spec
            //       page 5 top paragraphs, and if so broadcast to servers, AFTER
            //       being processed
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
