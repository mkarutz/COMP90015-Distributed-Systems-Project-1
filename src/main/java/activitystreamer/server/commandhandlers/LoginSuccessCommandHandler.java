package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.LoginSuccessCommand;
import activitystreamer.core.command.RegisterSuccessCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginSuccessCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private final UserAuthService userAuthService;
    private final ServerAuthService serverAuthService;
    private final ConnectionManager connectionManager;

    @Inject
    public LoginSuccessCommandHandler(UserAuthService userAuthService,
                                      ServerAuthService serverAuthService,
                                      ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LoginSuccessCommand) {
            LoginSuccessCommand cmd = (LoginSuccessCommand) command;

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!connectionManager.isParentConnection(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not parent connection."));
                connectionManager.closeConnection(conn);
                return true;
            }

            userAuthService.loginSuccess(cmd.getUsername(), cmd.getSecret());
            return true;
        } else {
            return false;
        }
    }
}
