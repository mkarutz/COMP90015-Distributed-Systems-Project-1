package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.client.services.*;

public class RedirectCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof RedirectCommand) {
            RedirectCommand redirectCommand = (RedirectCommand)command;

            // TODO: Client should close connection and reconnect via the
            // supplied host name/port as according to the spec Page 4, under
            // "REDIRECT" heading

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
