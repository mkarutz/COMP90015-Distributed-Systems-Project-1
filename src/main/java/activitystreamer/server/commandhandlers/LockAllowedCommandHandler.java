package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockAllowedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private final UserAuthService userAuthService;
    private final ServerAuthService serverAuthService;
    private final BroadcastService broadcastService;
    private final ConnectionManager connectionManager;

    @Inject
    public LockAllowedCommandHandler(UserAuthService userAuthService,
                                     ServerAuthService serverAuthService,
                                     BroadcastService broadcastService,
                                     ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
        this.broadcastService = broadcastService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LockAllowedCommand) {
            LockAllowedCommand cmd = (LockAllowedCommand) command;

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                connectionManager.closeConnection(conn);
                return true;
            }

            userAuthService.lockAllowed(cmd.getUsername(), cmd.getSecret(), cmd.getServerId());
            broadcastService.broadcastToServers(cmd, conn);

            return true;
        } else {
            return false;
        }
    }
}
