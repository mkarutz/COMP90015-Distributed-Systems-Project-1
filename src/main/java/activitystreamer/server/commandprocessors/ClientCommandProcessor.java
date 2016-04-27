package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandhandlers.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;

public class ClientCommandProcessor extends CommandProcessor {
    public ClientCommandProcessor() {
        super();

        // Client specific command handlers
        incomingHandlers.add(new LogoutCommandHandler());

        //outgoingHandlers.add(new LoginSuccessCommandHandler());
        outgoingHandlers.add(new RedirectCommandHandler());
    }
}
