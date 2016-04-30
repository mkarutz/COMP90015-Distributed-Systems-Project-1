package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class LoginCommandHandler implements ICommandHandler {
    private final UserAuthService userAuthService;
    private final RemoteServerStateService remoteServerStateService;
    private final ConnectionManager connectionManager;

    @Inject
    public LoginCommandHandler(UserAuthService userAuthService,
                               RemoteServerStateService remoteServerStateService,
                               ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.remoteServerStateService = remoteServerStateService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LoginCommand) {
            LoginCommand loginCommand = (LoginCommand) command;

            if (loginCommand.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (loginCommand.getUsername().equals(UserAuthService.ANONYMOUS)) {
                userAuthService.loginAsAnonymous(conn);
                sendLoginSuccess(conn, loginCommand.getUsername());
                remoteServerStateService.loadBalance(conn);
                return true;
            }

            if (loginCommand.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (userAuthService.login(conn, loginCommand.getUsername(), loginCommand.getSecret())) {
                sendLoginSuccess(conn, loginCommand.getUsername());
                remoteServerStateService.loadBalance(conn);
            } else {
                conn.pushCommand(new LoginFailedCommand("Username or secret incorrect"));
                connectionManager.closeConnection(conn);
            }

            return true;
        } else {
            return false;
        }
    }

    private void sendLoginSuccess(Connection conn, String username) {
        LoginSuccessCommand cmd = new LoginSuccessCommand("Logged in successfully as user " + username);
        conn.pushCommand(cmd);
    }
}
