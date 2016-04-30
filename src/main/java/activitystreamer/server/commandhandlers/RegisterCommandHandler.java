package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.RegisterCommand;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class RegisterCommandHandler implements ICommandHandler {

    UserAuthService userAuthService;

    @Inject
    public RegisterCommandHandler(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof RegisterCommand) {
            RegisterCommand cmd = (RegisterCommand) command;

            if (cmd.getUsername() == null || cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username and secret must be present."));
                conn.close();
                return true;
            }

            if (!userAuthService.register(cmd.getUsername(), cmd.getSecret(), conn)) {
                conn.pushCommand(new RegisterFailedCommand("Username " + cmd.getUsername() + " already exists."));
            }

            return true;
        } else {
            return false;
        }
    }
}
