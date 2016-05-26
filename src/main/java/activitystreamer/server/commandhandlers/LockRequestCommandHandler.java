package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.contracts.BroadcastService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockRequestCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private final UserAuthService userAuthService;
    private final ServerAuthService serverAuthService;
    private final ConnectionManager connectionManager;
    private final BroadcastService broadcastService;

    @Inject
    public LockRequestCommandHandler(UserAuthService userAuthService,
                                     ServerAuthService serverAuthService,
                                     ConnectionManager connectionManager,
                                     BroadcastService broadcastService) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
        this.connectionManager = connectionManager;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LockRequestCommand) {
            LockRequestCommand cmd = (LockRequestCommand) command;

            if (userAuthService.isLoggedIn(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Unexpected register from client."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Server not authenticated."));
                connectionManager.closeConnection(conn);
                return true;
            }

            log.debug("FOOBAR");
            if (connectionManager.isParentConnection(conn)) {
                log.debug("BAZBUZ");
                broadcastService.broadcastToServers(command, conn);
                return true;
            }

            if (!userAuthService.register(cmd.getUsername(), cmd.getSecret(), conn)) {
                conn.pushCommand(new LockDeniedCommand(cmd.getUsername(), cmd.getSecret()));
                return true;
            }

            return true;
        } else {
            return false;
        }
    }
}
