package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.contracts.IUserAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockRequestCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private IUserAuthService userAuthService;
    private IServerAuthService serverAuthService;

    public LockRequestCommandHandler(IUserAuthService userAuthService,
                                     IServerAuthService serverAuthService) {
        this.userAuthService = userAuthService;
        this.serverAuthService = serverAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockRequestCommand) {
            LockRequestCommand cmd = (LockRequestCommand) command;

            if (cmd.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                conn.close();
                return true;
            }

            if (cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                conn.close();
                return true;
            }

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                conn.close();
                return true;
            }

            userAuthService.lockRequest(cmd.getUsername(), cmd.getSecret());
            return true;
        } else {
            return false;
        }
    }
}
