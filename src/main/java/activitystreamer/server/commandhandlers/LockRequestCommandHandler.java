package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockRequestCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService rUserAuthService;
    private ServerAuthService rServerAuthService;
    private ConnectionStateService rConnectionStateService;

    public LockRequestCommandHandler(UserAuthService rUserAuthService,
                                     ServerAuthService rServerAuthService, ConnectionStateService rConnectionStateService) {
        this.rUserAuthService = rUserAuthService;
        this.rServerAuthService = rServerAuthService;
        this.rConnectionStateService=rConnectionStateService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LockRequestCommand) {

            if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.SERVER)){
                conn.pushCommand(new InvalidMessageCommand("Imposter SERVER."));
                conn.close();
                return true;
            }

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

            if (!rServerAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                conn.close();
                return true;
            }

            rUserAuthService.lockRequest(cmd.getUsername(), cmd.getSecret());
            return true;
        } else {
            return false;
        }
    }
}
