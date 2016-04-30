package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ServerAuthService;
import com.google.inject.Inject;

public class ActivityBroadcastCommandHandler implements ICommandHandler {

    private ServerAuthService serverAuthService;
    private BroadcastService broadcastService;

    @Inject
    public ActivityBroadcastCommandHandler(ServerAuthService serverAuthService,
                                           BroadcastService broadcastService) {
        this.serverAuthService = serverAuthService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            ActivityBroadcastCommand cmd = (ActivityBroadcastCommand) command;

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authorised."));
                conn.close();
                return true;
            }

            if (!cmd.getActivity().has("authenticated_user")) {
                conn.pushCommand(new InvalidMessageCommand("Invalid activity object."));
                conn.close();
                return true;
            }

            broadcastService.broadcastToAll(cmd, conn);
            return true;
        } else {
            return false;
        }
    }
}
