package activitystreamer.server.commandprocessors;

import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class MainCommandProcessor extends CommandProcessor {
    @Inject
    public MainCommandProcessor(RemoteServerStateService remoteServerStateService,
                                UserAuthService userAuthService,
                                ServerAuthService serverAuthService,
                                BroadcastService broadcastService) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(serverAuthService, broadcastService));
        handlers.add(new ActivityMessageCommandHandler(userAuthService, broadcastService));
        handlers.add(new AuthenticateCommandHandler(serverAuthService));
        handlers.add(new AuthenticationFailCommandHandler());
        handlers.add(new LockAllowedCommandHandler(userAuthService, broadcastService));
        handlers.add(new LockDeniedCommandHandler(userAuthService, broadcastService));
        handlers.add(new LockRequestCommandHandler(userAuthService, serverAuthService));
        handlers.add(new LoginCommandHandler(userAuthService, remoteServerStateService));
        handlers.add(new RegisterCommandHandler(userAuthService));
        handlers.add(new ServerAnnounceCommandHandler(serverAuthService, remoteServerStateService, broadcastService));
        handlers.add(new LogoutCommandHandler(userAuthService));
    }
}
