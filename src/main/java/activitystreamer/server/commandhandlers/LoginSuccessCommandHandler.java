package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class LoginSuccessCommandHandler implements ICommandHandler {

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        return false;
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LoginSuccessCommand) {
            conn.pushCommandDirect(command);

            // TODO: Check server load according to SPEC page 4, line 1
            //       Send redirect if necessary
            //
            // ...conn.pushCommand(new Redi)

            return true;
        } else {
            return false;
        }
    }
}
