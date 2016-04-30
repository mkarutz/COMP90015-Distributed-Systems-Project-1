package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;

public abstract class CommandProcessor {
    protected List<ICommandHandler> handlers = new ArrayList<>();

    public CommandProcessor() {
        InvalidMessageCommandHandler invalidMessageCommandHandler = new InvalidMessageCommandHandler();
        handlers.add(invalidMessageCommandHandler);
    }

    public synchronized void processCommandIncoming(Connection connection, Command command) {
        boolean handled = false;
        for (ICommandHandler h : handlers) {
            if (h.handleCommand(command, connection)) {
                handled = true;
                break;
            }
        }

        if (!handled) {
            Command invalidCommand = new InvalidMessageCommand("Command type is invalid.");
            connection.pushCommand(invalidCommand);
            connection.close();
        }
    }
}
