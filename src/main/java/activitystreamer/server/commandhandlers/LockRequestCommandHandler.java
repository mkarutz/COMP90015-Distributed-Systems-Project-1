package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockRequestCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rAuthService;

    public LockRequestCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof LockRequestCommand) {
            LockRequestCommand lCommand = (LockRequestCommand)command;

            // Register lock request with auth service
            UserAuthService.LockRequestResult r = rAuthService.lockRequest(lCommand.getUsername(), lCommand.getSecret());
            if (r == UserAuthService.LockRequestResult.SUCCESS) {
                // TODO: Send lock allowed
            } else {
                // TODO: Send lock denied
            }

            // Broadcast out
            conn.getCommandBroadcaster().broadcast(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LockRequestCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
