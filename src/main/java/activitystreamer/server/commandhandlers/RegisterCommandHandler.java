package activitystreamer.server.commandhandlers;

import activitystreamer.Server;
import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.RegisterCommand;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class RegisterCommandHandler implements ICommandHandler {

    private final UserAuthService userAuthService;
    private final ServerAuthService serverAuthService;
    private final ConnectionManager connectionManager;

    @Inject
    public RegisterCommandHandler(UserAuthService userAuthService,
                                  ServerAuthService serverAuthService,
                                  ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof RegisterCommand) {
            RegisterCommand cmd = (RegisterCommand) command;

            if (userAuthService.isLoggedIn(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Unexpected register from client."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (cmd.getUsername() == null || cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username and secret must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!userAuthService.register(cmd.getUsername(), cmd.getSecret(), conn)) {
                conn.pushCommand(new RegisterFailedCommand("Username " + cmd.getUsername() + " already exists."));
                connectionManager.closeConnection(conn);
                return true;
            }

            return true;
        } else {
            return false;
        }
    }
}
