package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;
import activitystreamer.server.commandhandlers.InvalidMessageCommandHandler;

public abstract class CommandProcessor {
    protected List<ICommandHandler> handlers = new ArrayList<>();

    public CommandProcessor() {
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
            invalidMessage(connection, command);
        }
    }

    public abstract void invalidMessage(Connection connection, Command command);
}
