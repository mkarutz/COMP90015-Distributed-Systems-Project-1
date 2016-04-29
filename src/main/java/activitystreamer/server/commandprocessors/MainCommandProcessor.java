package activitystreamer.server.commandprocessors;

import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;

public class MainCommandProcessor extends CommandProcessor {
    public MainCommandProcessor(RemoteServerStateService rServerService,
                                IUserAuthService rIUserAuthService,
                                IServerAuthService rIServerAuthService,
                                ConnectionStateService rConnectionStateService) {
        super();

        handlers.add(new ActivityBroadcastCommandHandler(rIServerAuthService, rConnectionStateService));
        handlers.add(new ActivityMessageCommandHandler(rIUserAuthService, rConnectionStateService));
        handlers.add(new AuthenticateCommandHandler(rIServerAuthService));
        handlers.add(new AuthenticationFailCommandHandler());
        handlers.add(new LockAllowedCommandHandler(rIUserAuthService, rConnectionStateService));
        handlers.add(new LockDeniedCommandHandler(rIUserAuthService, rConnectionStateService));
        handlers.add(new LockRequestCommandHandler(rIUserAuthService, rIServerAuthService));
        handlers.add(new LoginCommandHandler(rIUserAuthService, rServerService, rConnectionStateService));
        handlers.add(new RegisterCommandHandler(rIUserAuthService));
        handlers.add(new ServerAnnounceCommandHandler(rServerService, rConnectionStateService));
        handlers.add(new LogoutCommandHandler(rIUserAuthService));
    }
}
