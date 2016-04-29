package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IServerAuthService;

public class ActivityBroadcastCommandHandler implements ICommandHandler {

    private IServerAuthService serverAuthService;
    private IBroadcastService broadcastService;

    public ActivityBroadcastCommandHandler(IServerAuthService serverAuthService,
                                           IBroadcastService broadcastService) {
        this.serverAuthService = serverAuthService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
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
