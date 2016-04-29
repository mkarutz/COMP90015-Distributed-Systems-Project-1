package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.RegisterCommand;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.IUserAuthService;
import activitystreamer.server.services.*;

public class RegisterCommandHandler implements ICommandHandler {

    private IUserAuthService rAuthService;
    private ConnectionStateService rConnectionStateService;

    public RegisterCommandHandler(IUserAuthService rAuthService, ConnectionStateService rConnectionStateService) {
        this.rAuthService = rAuthService;
        this.rConnectionStateService=rConnectionStateService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof RegisterCommand) {

            if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.UNKNOWN)){
                conn.pushCommand(new InvalidMessageCommand("Imposter."));
                conn.close();
                return true;
            }

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
