package activitystreamer.server.commandprocessors;

import activitystreamer.core.command.ICommandBroadcaster;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.server.commandhandlers.ActivityBroadcastCommandHandler;
import activitystreamer.server.commandhandlers.ActivityMessageCommandHandler;
import activitystreamer.server.commandhandlers.LogoutCommandHandler;
import activitystreamer.server.commandhandlers.RedirectCommandHandler;
import activitystreamer.server.services.UserAuthService;

public class ClientCommandProcessor extends CommandProcessor {
    public ClientCommandProcessor(UserAuthService rAuthService, ICommandBroadcaster rBroadcastService) {
        super();

        // Client specific command handlers
        incomingHandlers.add(new LogoutCommandHandler());
        incomingHandlers.add(new ActivityMessageCommandHandler(rAuthService, rBroadcastService));

        //outgoingHandlers.add(new LoginSuccessCommandHandler());
        outgoingHandlers.add(new RedirectCommandHandler());
        outgoingHandlers.add(new ActivityBroadcastCommandHandler());
    }
}
