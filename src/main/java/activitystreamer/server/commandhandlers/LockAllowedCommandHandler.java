package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockAllowedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rAuthService;
    private ConnectionStateService rConnectionStateService;

    public LockAllowedCommandHandler(UserAuthService rAuthService, ConnectionStateService rConnectionStateService) {
        this.rAuthService = rAuthService;
        this.rConnectionStateService = rConnectionStateService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof LockAllowedCommand) {
            LockAllowedCommand lCommand = (LockAllowedCommand)command;

            // Register lock allowed with auth service
            rAuthService.lockAllowed(lCommand.getUsername(), lCommand.getSecret(), lCommand.getServerId());

            // Broadcast out
            rConnectionStateService.broadcastToAll(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LockAllowedCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
