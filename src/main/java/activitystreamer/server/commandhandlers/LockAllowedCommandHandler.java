package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockAllowedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rAuthService;

    public LockAllowedCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockAllowedCommand) {
            LockAllowedCommand lCommand = (LockAllowedCommand)command;

            // Register lock allowed with auth service
            rAuthService.putLockAllowed(lCommand.getUsername(), lCommand.getSecret(), lCommand.getServerId());

            // Broadcast out
            conn.getCommandBroadcaster().broadcastToServers(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
