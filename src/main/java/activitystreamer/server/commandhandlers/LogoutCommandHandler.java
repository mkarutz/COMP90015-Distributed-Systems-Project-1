package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IUserAuthService;

public class LogoutCommandHandler implements ICommandHandler {
    private IUserAuthService rAuthService;

    public LogoutCommandHandler(IUserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LogoutCommand) {
            rAuthService.logout(conn);
            conn.close();
            return true;
        } else {
            return false;
        }
    }
}
