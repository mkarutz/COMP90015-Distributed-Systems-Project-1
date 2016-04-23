package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;

/* Design pattern inspired by
   http://stackoverflow.com/questions/1477471/design-pattern-for-handling-multiple-message-types */

public class PendingCommandProcessor implements ICommandProcessor {
    private List<ICommandHandler> handlers = new ArrayList<ICommandHandler>();
    private Connection connectionHandle;

    public PendingCommandProcessor(Connection connectionHandle) {
        this.connectionHandle = connectionHandle;

        handlers.add(new AuthenticateCommandHandler());
        handlers.add(new RegisterCommandHandler());
        handlers.add(new LoginCommandHandler());
        handlers.add(new ActivityMessageCommandHandler());
    }

    public void processCommand(ICommand msg, Connection conn) {
        boolean handled=false;
        for (ICommandHandler h : handlers) {
            if (h.handleCommand(msg,conn)) {
                handled = true;
                break;
            }
        }

        /* By default, if not handled, we send an INVALID_MESSAGE response and
         * close the connection */
        if (!handled) {
            ICommand invalidCommand = new InvalidMessageCommand("Command type was invalid for the current command processor.");
            connectionHandle.pushCommand(invalidCommand);
            connectionHandle.close();
        }
    }
}
