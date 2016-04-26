package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class LoginCommandHandler implements ICommandHandler {

    UserAuthService rAuthService;

    public LoginCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            LoginCommand loginCommand = (LoginCommand)command;

            // Check if user stored
            if (rAuthService.isUserRegistered(loginCommand.getUsername(), loginCommand.getSecret())) {
                // User and secret match, logged in successfully
                LoginSuccessCommand cmd = new LoginSuccessCommand("Logged in successfully as user " + loginCommand.getUsername());
                conn.pushCommand(cmd);
            } else {
                // User or secret do not match
                LoginFailedCommand cmd = new LoginFailedCommand("Username or secret incorrect");
                conn.pushCommand(cmd);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof LoginCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
