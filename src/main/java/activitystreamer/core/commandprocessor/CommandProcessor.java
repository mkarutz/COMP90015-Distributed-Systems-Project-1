package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.server.*;

public abstract class CommandProcessor {
    protected List<ICommandHandler> handlers = new ArrayList<ICommandHandler>();

    public CommandProcessor() {
        // All command processors handle invalid messages
        handlers.add(new InvalidMessageCommandHandler());
    }

    public void processCommand(Connection connection, ICommand command) {
        boolean handled = false;
        for (ICommandHandler h : handlers) {
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
