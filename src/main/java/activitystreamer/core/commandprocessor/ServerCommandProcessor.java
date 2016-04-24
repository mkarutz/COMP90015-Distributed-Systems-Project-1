package activitystreamer.core.commandprocessor;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(RemoteServerStateService rServerService, UserAuthService rAuthService) {
        super();

        // Server specific command handlers
        handlers.add(new ActivityBroadcastCommandHandler());
        handlers.add(new AuthenticationFailCommandHandler());
        handlers.add(new LockAllowedCommandHandler(rAuthService));
        handlers.add(new LockDeniedCommandHandler(rAuthService));
        handlers.add(new LockRequestCommandHandler(rAuthService));
        handlers.add(new ServerAnnounceCommandHandler(rServerService));
    }
}
