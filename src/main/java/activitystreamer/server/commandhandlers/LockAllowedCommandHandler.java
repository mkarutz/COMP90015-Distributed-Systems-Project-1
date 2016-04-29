package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IUserAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockAllowedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private IUserAuthService rAuthService;
    private IBroadcastService rIBroadcastService;

    public LockAllowedCommandHandler(IUserAuthService rAuthService, IBroadcastService rIBroadcastService) {
        this.rAuthService = rAuthService;
        this.rIBroadcastService = rIBroadcastService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockAllowedCommand) {
            LockAllowedCommand lCommand = (LockAllowedCommand)command;

            // Register lock allowed with auth service
            rAuthService.lockAllowed(lCommand.getUsername(), lCommand.getSecret(), lCommand.getServerId());

            // Broadcast out
            rIBroadcastService.broadcastToServers(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
