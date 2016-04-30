package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticateCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private final ServerAuthService serverAuthService;
    private final ConnectionManager connectionManager;

    @Inject
    public AuthenticateCommandHandler(ServerAuthService serverAuthService,
                                      ConnectionManager connectionManager) {
        this.serverAuthService = serverAuthService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            AuthenticateCommand cmd = (AuthenticateCommand) command;

            if (cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present"));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!serverAuthService.authenticate(conn, cmd.getSecret())) {
                conn.pushCommand(new AuthenticationFailCommand("Incorrect secret"));
                connectionManager.closeConnection(conn);
            }

            return true;
        } else {
            return false;
        }
    }
}
