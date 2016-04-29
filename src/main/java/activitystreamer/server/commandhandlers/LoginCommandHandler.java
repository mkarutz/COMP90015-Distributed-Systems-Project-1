package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;

public class LoginCommandHandler implements ICommandHandler {
    private final IUserAuthService rAuthService;
    private final RemoteServerStateService rServerStateService;
    private final ConnectionStateService rConnectionStateService;

    public LoginCommandHandler(IUserAuthService rAuthService,
                               RemoteServerStateService rServerStateService,
                               ConnectionStateService rConnectionStateService) {
        this.rAuthService = rAuthService;
        this.rServerStateService = rServerStateService;
        this.rConnectionStateService = rConnectionStateService;
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

            if (loginCommand.getUsername().equals(IUserAuthService.ANONYMOUS)) {
                rAuthService.loginAsAnonymous(conn);
                sendLoginSuccess(conn, loginCommand.getUsername());
                // Dealing with a client connection
                rConnectionStateService.setConnectionType(conn, ConnectionStateService.ConnectionType.CLIENT);

                loadBalance(conn);
                return true;
            }

            if (loginCommand.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                conn.close();
                return true;
            }

            if (rAuthService.login(conn, loginCommand.getUsername(), loginCommand.getSecret())) {
                sendLoginSuccess(conn, loginCommand.getUsername());
                // Dealing with a client connection
                rConnectionStateService.setConnectionType(conn, ConnectionStateService.ConnectionType.CLIENT);

                loadBalance(conn);
            } else {
                conn.pushCommand(new LoginFailedCommand("Username or secret incorrect"));
                conn.close();
            }

            return true;
        } else {
            return false;
        }
    }

    private void loadBalance(Connection conn) {
        synchronized (rServerStateService) {
            ServerState redirectTo;
            if ((redirectTo = rServerStateService.getServerToRedirectTo()) != null) {
                RedirectCommand cmd = new RedirectCommand(redirectTo.getHostname(), redirectTo.getPort());
                conn.pushCommand(cmd);
                conn.close();
            }
        }
    }

    private void sendLoginSuccess(Connection conn, String username) {
        LoginSuccessCommand cmd = new LoginSuccessCommand("Logged in successfully as user " + username);
        conn.pushCommand(cmd);
    }
}
