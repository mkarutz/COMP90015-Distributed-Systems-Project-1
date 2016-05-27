package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.contracts.*;
import com.google.inject.Inject;

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
        handlers.add(new AuthenticateCommandHandler(userAuthService, serverAuthService, connectionManager));
        handlers.add(new AuthenticationFailCommandHandler(serverAuthService, connectionManager));
        handlers.add(new LockAllowedCommandHandler());
        handlers.add(new LockDeniedCommandHandler());
        handlers.add(new LockRequestCommandHandler(userAuthService, serverAuthService, connectionManager, broadcastService));
        handlers.add(new LoginCommandHandler(userAuthService, serverAuthService, remoteServerStateService, connectionManager));
        handlers.add(new RegisterCommandHandler(userAuthService, serverAuthService, connectionManager, remoteServerStateService));
        handlers.add(new ServerAnnounceCommandHandler(remoteServerStateService, broadcastService, serverAuthService, connectionManager));
        handlers.add(new LogoutCommandHandler(userAuthService, connectionManager));
        handlers.add(new RegisterSuccessCommandHandler(userAuthService, serverAuthService, connectionManager));
        handlers.add(new RegisterFailedCommandHandler(userAuthService, serverAuthService, connectionManager));
        handlers.add(new LoginSuccessCommandHandler(userAuthService, serverAuthService, connectionManager));
        handlers.add(new LoginFailedCommandHandler(userAuthService, serverAuthService, connectionManager));
    }

    @Override
    public void invalidMessage(Connection connection, Command command) {
        Command invalidCommand = new InvalidMessageCommand("Command type is invalid.");
        connection.pushCommand(invalidCommand);
        connectionManager.closeConnection(connection);
    }
}
