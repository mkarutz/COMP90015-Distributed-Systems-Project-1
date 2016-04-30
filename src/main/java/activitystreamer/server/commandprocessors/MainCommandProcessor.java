package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.contracts.*;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class MainCommandProcessor extends CommandProcessor {
    private final ConnectionManager connectionManager;

    @Inject
    public MainCommandProcessor(ServerAuthService serverAuthService, UserAuthService userAuthService,
                                ConnectionManager connectionManager, BroadcastService broadcastService,
                                RemoteServerStateService remoteServerStateService) {
        super();
        this.connectionManager = connectionManager;
        handlers.add(new ActivityBroadcastCommandHandler(serverAuthService, broadcastService, connectionManager));
        handlers.add(new ActivityMessageCommandHandler(userAuthService, broadcastService, connectionManager));
        handlers.add(new AuthenticateCommandHandler(serverAuthService, connectionManager));
        handlers.add(new AuthenticationFailCommandHandler(connectionManager));
        handlers.add(new LockAllowedCommandHandler(userAuthService, broadcastService));
        handlers.add(new LockDeniedCommandHandler(userAuthService, broadcastService));
        handlers.add(new LockRequestCommandHandler(userAuthService, serverAuthService, connectionManager));
        handlers.add(new LoginCommandHandler(userAuthService, remoteServerStateService, connectionManager));
        handlers.add(new RegisterCommandHandler(userAuthService, connectionManager));
        handlers.add(new ServerAnnounceCommandHandler(remoteServerStateService, broadcastService, serverAuthService, connectionManager));
        handlers.add(new LogoutCommandHandler(userAuthService, connectionManager));
    }

    @Override
    public void invalidMessage(Connection connection, Command command) {
        Command invalidCommand = new InvalidMessageCommand("Command type is invalid.");
        connection.pushCommand(invalidCommand);
        connectionManager.closeConnection(connection);
    }
}
