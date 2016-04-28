package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.RegisterCommand;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.IUserAuthService;

public class RegisterCommandHandler implements ICommandHandler {

    IUserAuthService rAuthService;

    public RegisterCommandHandler(IUserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {
            RegisterCommand registerCommand = (RegisterCommand) command;

            if (registerCommand.getUsername() == null || registerCommand.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username and secret must be present."));
                conn.close();
                return true;
            }

            if (!rAuthService.register(registerCommand.getUsername(), registerCommand.getSecret(), conn)) {
                conn.pushCommand(new RegisterFailedCommand("Username " + registerCommand.getUsername() + "  already exists."));
            }

            return true;
        } else {
            return false;
        }
    }
}
