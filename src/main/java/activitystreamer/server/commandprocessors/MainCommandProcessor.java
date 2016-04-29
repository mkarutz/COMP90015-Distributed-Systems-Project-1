package activitystreamer.server.commandprocessors;

import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;

public class MainCommandProcessor extends CommandProcessor {
    public MainCommandProcessor(RemoteServerStateService remoteServerStateService,
                                IUserAuthService userAuthService,
                                IServerAuthService serverAuthService,
                                ConnectionStateService connectionStateService) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(serverAuthService, connectionStateService));
        handlers.add(new ActivityMessageCommandHandler(userAuthService, connectionStateService));
        handlers.add(new AuthenticateCommandHandler(serverAuthService));
        handlers.add(new AuthenticationFailCommandHandler());
        handlers.add(new LockAllowedCommandHandler(userAuthService, connectionStateService));
        handlers.add(new LockDeniedCommandHandler(userAuthService, connectionStateService));
        handlers.add(new LockRequestCommandHandler(userAuthService, serverAuthService));
        handlers.add(new LoginCommandHandler(userAuthService, remoteServerStateService, connectionStateService));
        handlers.add(new RegisterCommandHandler(userAuthService));
        handlers.add(new ServerAnnounceCommandHandler(serverAuthService, remoteServerStateService, connectionStateService));
        handlers.add(new LogoutCommandHandler(userAuthService));
    }
}
