package activitystreamer.core.commandprocessor;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor(RemoteServerStateService rServerService) {
        super();
        // Uncomment as we implement command handlers
        handlers.add(new ActivityBroadcastCommandHandler());
        handlers.add(new AuthenticationFailCommandHandler());
        //handlers.add(new LockAllowedCommandHandler());
        //handlers.add(new LockDeniedCommandHandler());
        //handlers.add(new LockRequestCommandHandler());
        handlers.add(new ServerAnnounceCommandHandler(rServerService));
    }
}
