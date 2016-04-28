package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.ICommandBroadcaster;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.*;

public class MainCommandProcessor extends CommandProcessor {
    public MainCommandProcessor(RemoteServerStateService rServerService,
                                UserAuthService rUserAuthService,
                                ServerAuthService rServerAuthService,
                                ConnectionStateService rConnectionStateService) {
        super();

        addIncomingOutgoing(new ActivityBroadcastCommandHandler(rConnectionStateService));
        addIncomingOutgoing(new ActivityMessageCommandHandler(rUserAuthService, rConnectionStateService));
        addIncomingOutgoing(new AuthenticateCommandHandler(rServerService, rUserAuthService, rServerAuthService, rConnectionStateService));
        addIncomingOutgoing(new AuthenticationFailCommandHandler());
        addIncomingOutgoing(new LockAllowedCommandHandler(rUserAuthService, rConnectionStateService));
        addIncomingOutgoing(new LockDeniedCommandHandler(rUserAuthService, rConnectionStateService));
        addIncomingOutgoing(new LockRequestCommandHandler(rUserAuthService, rServerAuthService));
        addIncomingOutgoing(new LoginCommandHandler(rUserAuthService, rServerService, rConnectionStateService));
        addIncomingOutgoing(new LoginFailedCommandHandler());
        addIncomingOutgoing(new LoginSuccessCommandHandler());
        addIncomingOutgoing(new LogoutCommandHandler());
        addIncomingOutgoing(new RedirectCommandHandler());
        addIncomingOutgoing(new RegisterCommandHandler(rUserAuthService));
        addIncomingOutgoing(new RegisterFailedCommandHandler());
        addIncomingOutgoing(new RegisterSuccessCommandHandler());
        addIncomingOutgoing(new ServerAnnounceCommandHandler(rServerService, rConnectionStateService));
    }

    private void addIncomingOutgoing(ICommandHandler handler) {
        incomingHandlers.add(handler);
        outgoingHandlers.add(handler);
    }
}
