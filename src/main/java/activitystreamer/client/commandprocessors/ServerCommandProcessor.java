package activitystreamer.client.commandprocessors;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.client.commandhandlers.*;

public class ServerCommandProcessor extends CommandProcessor {
    public ServerCommandProcessor() {
        super();

        //handlers.add(new ActivityBroadcastCommandHandler());
        //handlers.add(new LoginFailedCommandHandler());
        //handlers.add(new LoginSuccessCommandHandler());
        //handlers.add(new RedirectCommandHandler());
    }
}
