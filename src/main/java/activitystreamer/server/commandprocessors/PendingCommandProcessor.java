package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

/**
 * Design pattern inspired by
 * http://stackoverflow.com/questions/1477471/design-pattern-for-handling-multiple-message-types
 */
public class PendingCommandProcessor extends CommandProcessor {
    public PendingCommandProcessor(RemoteServerStateService rServerService, UserAuthService rAuthService) {
        super();
        incomingHandlers.add(new AuthenticateCommandHandler(rServerService, rAuthService));
        incomingHandlers.add(new RegisterCommandHandler());
        incomingHandlers.add(new LoginCommandHandler());
        incomingHandlers.add(new ActivityMessageCommandHandler());

        outgoingHandlers.add(new AuthenticationFailCommandHandler());
    }
}
