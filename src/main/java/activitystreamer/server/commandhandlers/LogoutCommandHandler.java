package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;

public class LogoutCommandHandler implements ICommandHandler {
    private UserAuthService userAuthService;

    @Inject
    public LogoutCommandHandler(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LogoutCommand) {
            userAuthService.logout(conn);
            conn.close();
            return true;
        } else {
            return false;
        }
    }
}
