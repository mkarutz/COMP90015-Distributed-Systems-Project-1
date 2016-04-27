package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.*;
import activitystreamer.server.commandprocessors.*;

public class LoginCommandHandler implements ICommandHandler {
    private final UserAuthService rAuthService;
    private final RemoteServerStateService rServerStateService;
    private final ICommandBroadcaster rBroadcastService;

    public LoginCommandHandler(UserAuthService rAuthService,
                               RemoteServerStateService rServerStateService,
                               ICommandBroadcaster rBroadcastService) {
        this.rAuthService = rAuthService;
        this.rServerStateService = rServerStateService;
        this.rBroadcastService = rBroadcastService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            LoginCommand loginCommand = (LoginCommand) command;

            if (loginCommand.getUsername() == null) {
                InvalidMessageCommand cmd = new InvalidMessageCommand("Username must be present.");
                conn.pushCommand(cmd);
                conn.close();
                return true;
            }

            if (loginCommand.getUsername().equals(UserAuthService.ANONYMOUS)) {
                rAuthService.loginAsAnonymous(conn);
                sendLoginSuccess(conn, loginCommand.getUsername());
            }

            if (rAuthService.isUserRegistered(loginCommand.getUsername(), loginCommand.getSecret())) {
                sendLoginSuccess(conn, loginCommand.getUsername());
                conn.setCommandProcessor(new ClientCommandProcessor(rAuthService, rBroadcastService));
                loadBalance(conn);
            } else {
                LoginFailedCommand cmd = new LoginFailedCommand("Username or secret incorrect");
                conn.pushCommand(cmd);
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

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
