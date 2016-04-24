package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.Connection;
import activitystreamer.util.Settings;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockDeniedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rAuthService;

    public LockDeniedCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockDeniedCommand) {
            LockDeniedCommand lCommand = (LockDeniedCommand)command;

            // Register lock denied with auth service
            rAuthService.putLockDenied(lCommand.getUsername(), lCommand.getSecret());

            // Broadcast out
            conn.getCommandBroadcaster().broadcastToServers(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
