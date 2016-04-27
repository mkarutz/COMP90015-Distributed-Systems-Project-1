package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class RegisterCommandHandler implements ICommandHandler {

    UserAuthService rAuthService;

    public RegisterCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            RegisterCommand registerCommand = (RegisterCommand) command;

            // User is attempting to register
            boolean result = rAuthService.putLocalLockRequest(registerCommand.getUsername(), registerCommand.getSecret(), conn);

            if (!result) {
                ICommand cmd = new RegisterFailedCommand("Username " + registerCommand.getUsername() + " already registered locally");
                conn.pushCommand(cmd);
            } else {
                // Broadcast lock requests
                ICommand cmd = new LockRequestCommand(registerCommand.getUsername(), registerCommand.getSecret());
                conn.getCommandBroadcaster().pushCommand(cmd);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
