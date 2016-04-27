package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

public class ClientCommandProcessor extends CommandProcessor {
    public ClientCommandProcessor(UserAuthService rAuthService) {
        super();

        // Client specific command handlers
        incomingHandlers.add(new LogoutCommandHandler());
        incomingHandlers.add(new ActivityMessageCommandHandler(rAuthService));

        //outgoingHandlers.add(new LoginSuccessCommandHandler());
        outgoingHandlers.add(new RedirectCommandHandler());
        outgoingHandlers.add(new ActivityBroadcastCommandHandler());
    }
}
