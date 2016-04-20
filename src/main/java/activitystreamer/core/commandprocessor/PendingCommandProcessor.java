package activitystreamer.core.commandprocessor;

/* Design pattern inspired by
   http://stackoverflow.com/questions/1477471/design-pattern-for-handling-multiple-message-types */

public class PendingCommandProcessor {
    private List<ICommandHandler> handlers = new List<ICommandHandler>();
    private Connection connectionHandle;

    public PendingCommandProcessor(Connection connectionHandle) {
        this.connectionHandle = connectionHandle;

        handlers.add(new AuthenticateCommandHandler());
        handlers.add(new RegisterCommandHandler());
        handlers.add(new LoginCommandHandler());
        handlers.add(new ActivityMessageCommandHandler());
    }

    public void processCommand(ICommand msg) {
        boolean handled;
        for (ICommandHandler h : handlers) {
            if (h.handleCommand(msg)) {
                handled = true;
                break;
            }
        }

        /* By default, if not handled, we send an INVALID_MESSAGE response and
         * close the connection */
        if (!handled) {
            ICommand invalidCommand = new InvalidMessageCommand("Command type was invalid for the current command processor.");
            connectionHandle.pushCommand(invalidCommand);
            connectionHandle.closeConnection();
        }
    }
}
