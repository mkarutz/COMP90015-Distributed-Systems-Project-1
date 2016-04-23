package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;

/**
 * Design pattern inspired by
 * http://stackoverflow.com/questions/1477471/design-pattern-for-handling-multiple-message-types
 */
public class PendingCommandProcessor implements ICommandProcessor {
    private List<ICommandHandler> handlers = new ArrayList<ICommandHandler>();

    public PendingCommandProcessor() {
        handlers.add(new AuthenticateCommandHandler());
        handlers.add(new RegisterCommandHandler());
        handlers.add(new LoginCommandHandler());
        handlers.add(new ActivityMessageCommandHandler());
    }

    @Override
    public void processCommand(Connection connection, ICommand command) {
        boolean handled=false;
        for (ICommandHandler h: handlers) {
            if (h.handleCommand(command, connection)) {
                handled = true;
                break;
            }
        }

        if (!handled) {
            ICommand invalidCommand = new InvalidMessageCommand("Command type was invalid for the current command processor.");
            connection.pushCommand(invalidCommand);
            connection.close();
        }
    }
}
