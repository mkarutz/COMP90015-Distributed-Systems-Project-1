package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockDeniedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rAuthService;
    private ConnectionStateService rConnectionStateService;

    public LockDeniedCommandHandler(UserAuthService rAuthService, ConnectionStateService rConnectionStateService) {
        this.rAuthService = rAuthService;
        this.rConnectionStateService = rConnectionStateService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockDeniedCommand) {

            if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.SERVER)){
                conn.pushCommand(new InvalidMessageCommand("Imposter SERVER."));
                conn.close();
                return true;
            }
            
            LockDeniedCommand lCommand = (LockDeniedCommand)command;

            // Register lock denied with auth service
            rAuthService.lockDenied(lCommand.getUsername(), lCommand.getSecret());

            // Broadcast out
            rConnectionStateService.broadcastToAll(lCommand, conn);

            return true;
        } else {
            return false;
        }
    }
}
