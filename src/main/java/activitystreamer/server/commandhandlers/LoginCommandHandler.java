package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class LoginCommandHandler implements ICommandHandler {
    private final UserAuthService userAuthService;
    private final RemoteServerStateService remoteServerStateService;

    @Inject
    public LoginCommandHandler(UserAuthService userAuthService,
                               RemoteServerStateService remoteServerStateService) {
        this.userAuthService = userAuthService;
        this.remoteServerStateService = remoteServerStateService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            LoginCommand loginCommand = (LoginCommand) command;

            if (loginCommand.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                conn.close();
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
                conn.close();
                return true;
            }

            if (userAuthService.login(conn, loginCommand.getUsername(), loginCommand.getSecret())) {
                sendLoginSuccess(conn, loginCommand.getUsername());
                remoteServerStateService.loadBalance(conn);
            } else {
                conn.pushCommand(new LoginFailedCommand("Username or secret incorrect"));
                conn.close();
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
