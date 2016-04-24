package activitystreamer.core.commandprocessor;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

/**
 * Design pattern inspired by
 * http://stackoverflow.com/questions/1477471/design-pattern-for-handling-multiple-message-types
 */
public class PendingCommandProcessor extends CommandProcessor {
    public PendingCommandProcessor(RemoteServerStateService rServerService, UserAuthService rAuthService) {
        super();
        handlers.add(new AuthenticateCommandHandler(rServerService, rAuthService));
        handlers.add(new RegisterCommandHandler());
        handlers.add(new LoginCommandHandler());
        handlers.add(new ActivityMessageCommandHandler());
    }
}
