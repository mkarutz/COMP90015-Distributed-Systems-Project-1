package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;

public abstract class CommandProcessor {
    protected List<ICommandHandler> incomingHandlers = new ArrayList<ICommandHandler>();
    protected List<ICommandHandler> outgoingHandlers = new ArrayList<ICommandHandler>();

    public CommandProcessor() {
        // All command processors handle invalid messages
        InvalidMessageCommandHandler invalidMessageCommandHandler = new InvalidMessageCommandHandler();
        incomingHandlers.add(invalidMessageCommandHandler);
        outgoingHandlers.add(invalidMessageCommandHandler);
    }

    public void processCommandIncoming(Connection connection, ICommand command) {
        boolean handled = false;
        for (ICommandHandler h : incomingHandlers) {
            if (h.handleCommandIncoming(command, connection)) {
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

    public void processCommandOutgoing(Connection connection, ICommand command) {
        boolean handled = false;
        for (ICommandHandler h : outgoingHandlers) {
            if (h.handleCommandOutgoing(command, connection)) {
                handled = true;
                break;
            }
        }

        if (!handled) {

        }
    }
}
