package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(RemoteServerStateService rServerService, UserAuthService rAuthService) {
        super();

        // Server specific command handlers
        ActivityBroadcastCommandHandler activityBroadcastCommandHandler = new ActivityBroadcastCommandHandler();
        AuthenticationFailCommandHandler authenticationFailCommandHandler = new AuthenticationFailCommandHandler();
        LockAllowedCommandHandler lockAllowedCommandHandler = new LockAllowedCommandHandler(rAuthService);
        LockDeniedCommandHandler lockDeniedCommandHandler = new LockDeniedCommandHandler(rAuthService);
        LockRequestCommandHandler lockRequestCommandHandler = new LockRequestCommandHandler(rAuthService);
        ServerAnnounceCommandHandler serverAnnounceCommandHandler = new ServerAnnounceCommandHandler(rServerService);

        incomingHandlers.add(activityBroadcastCommandHandler);
        incomingHandlers.add(authenticationFailCommandHandler);
        incomingHandlers.add(lockAllowedCommandHandler);
        incomingHandlers.add(lockDeniedCommandHandler);
        incomingHandlers.add(lockRequestCommandHandler);
        incomingHandlers.add(serverAnnounceCommandHandler);

        outgoingHandlers.add(activityBroadcastCommandHandler);
        outgoingHandlers.add(new AuthenticateCommandHandler(rServerService, rAuthService));
        outgoingHandlers.add(lockAllowedCommandHandler);
        outgoingHandlers.add(lockDeniedCommandHandler);
        outgoingHandlers.add(lockRequestCommandHandler);
        outgoingHandlers.add(serverAnnounceCommandHandler);
    }
}
