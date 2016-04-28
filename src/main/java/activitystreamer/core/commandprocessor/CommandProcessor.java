package activitystreamer.core.commandprocessor;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;

public abstract class CommandProcessor {
    protected List<ICommandHandler> handlers = new ArrayList<ICommandHandler>();

    public CommandProcessor() {
        // All command processors handle invalid messages
        InvalidMessageCommandHandler invalidMessageCommandHandler = new InvalidMessageCommandHandler();
        handlers.add(invalidMessageCommandHandler);
    }

    public void processCommandIncoming(Connection connection, ICommand command) {
        boolean handled = false;
        for (ICommandHandler h : handlers) {
            String filterError;
            if ((filterError = command.filter()) != null) {
                // There's been a validation issue
                ICommand invalidCommand = new InvalidMessageCommand(filterError);
                connection.pushCommand(invalidCommand);
                connection.close();
                handled = true;
            } else {
                if (h.handleCommand(command, connection)) {
                    handled = true;
                    break;
                }
            }
        }

        if (!handled) {
            ICommand invalidCommand = new InvalidMessageCommand("Command type is invalid.");
            connection.pushCommand(invalidCommand);
            connection.close();
        }
    }
}
