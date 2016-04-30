package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockDeniedCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private UserAuthService userAuthService;
    private BroadcastService broadcastService;

    @Inject
    public LockDeniedCommandHandler(UserAuthService userAuthService, BroadcastService broadcastService) {
        this.userAuthService = userAuthService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof LockDeniedCommand) {
            LockDeniedCommand cmd = (LockDeniedCommand) command;

            userAuthService.lockDenied(cmd.getUsername(), cmd.getSecret());
            broadcastService.broadcastToAll(cmd, conn);

            return true;
        } else {
            return false;
        }
    }
}
