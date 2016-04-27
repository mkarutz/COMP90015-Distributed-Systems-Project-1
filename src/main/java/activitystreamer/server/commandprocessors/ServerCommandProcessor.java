package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.ICommandBroadcaster;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.services.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(RemoteServerStateService rServerService,
                                  UserAuthService rUserAuthService,
                                  ServerAuthService rServerAuthService,
                                  ICommandBroadcaster rBroadcastService) {
        super();

        // Server specific command handlers
        ActivityBroadcastCommandHandler activityBroadcastCommandHandler = new ActivityBroadcastCommandHandler();
        AuthenticationFailCommandHandler authenticationFailCommandHandler = new AuthenticationFailCommandHandler();
        LockAllowedCommandHandler lockAllowedCommandHandler = new LockAllowedCommandHandler(rUserAuthService);
        LockDeniedCommandHandler lockDeniedCommandHandler = new LockDeniedCommandHandler(rUserAuthService);
        LockRequestCommandHandler lockRequestCommandHandler = new LockRequestCommandHandler(rUserAuthService, rServerAuthService);
        ServerAnnounceCommandHandler serverAnnounceCommandHandler = new ServerAnnounceCommandHandler(rServerService);

        incomingHandlers.add(activityBroadcastCommandHandler);
        incomingHandlers.add(authenticationFailCommandHandler);
        incomingHandlers.add(lockAllowedCommandHandler);
        incomingHandlers.add(lockDeniedCommandHandler);
        incomingHandlers.add(lockRequestCommandHandler);
        incomingHandlers.add(serverAnnounceCommandHandler);

        outgoingHandlers.add(activityBroadcastCommandHandler);
        outgoingHandlers.add(new AuthenticateCommandHandler(rServerService, rUserAuthService));
        outgoingHandlers.add(lockAllowedCommandHandler);
        outgoingHandlers.add(lockDeniedCommandHandler);
        outgoingHandlers.add(lockRequestCommandHandler);
        outgoingHandlers.add(serverAnnounceCommandHandler);
    }
}
