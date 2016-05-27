package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class LoginCommandHandler implements ICommandHandler {
    private final UserAuthService userAuthService;
    private final ServerAuthService serverAuthService;
    private final RemoteServerStateService remoteServerStateService;
    private final ConnectionManager connectionManager;

    @Inject
    public LoginCommandHandler(UserAuthService userAuthService,
                               ServerAuthService serverAuthService,
                               RemoteServerStateService remoteServerStateService,
                               ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
        this.remoteServerStateService = remoteServerStateService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LoginCommand) {
            LoginCommand loginCommand = (LoginCommand) command;

            if (remoteServerStateService.loadBalance(conn)) {
                return true;
            }

            if (userAuthService.isLoggedIn(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Unexpected login from client."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (loginCommand.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (loginCommand.getUsername().equals(UserAuthService.ANONYMOUS)) {
                userAuthService.loginAsAnonymous(conn);
                return true;
            }

            if (loginCommand.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            userAuthService.login(loginCommand.getUsername(), loginCommand.getSecret(), conn);
            return true;
        } else {
            return false;
        }
    }
}
