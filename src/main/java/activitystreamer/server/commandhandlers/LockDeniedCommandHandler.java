package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IUserAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockDeniedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private IUserAuthService rAuthService;
    private IBroadcastService rIBroadcastService;

    public LockDeniedCommandHandler(IUserAuthService rAuthService, IBroadcastService rIBroadcastService) {
        this.rAuthService = rAuthService;
        this.rIBroadcastService = rIBroadcastService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockDeniedCommand) {
            LockDeniedCommand lCommand = (LockDeniedCommand)command;

            // Register lock denied with auth service
            rAuthService.lockDenied(lCommand.getUsername(), lCommand.getSecret());

            // Broadcast out
            rIBroadcastService.broadcastToAll(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
