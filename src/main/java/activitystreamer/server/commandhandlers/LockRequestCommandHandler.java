package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;
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
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockRequestCommand) {
            LockRequestCommand lCommand = (LockRequestCommand)command;

            // Register lock request with auth service
            rAuthService.putLockRequest(lCommand.getUsername(), lCommand.getSecret());

            // Broadcast out
            conn.getCommandBroadcaster().broadcastToServers(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
