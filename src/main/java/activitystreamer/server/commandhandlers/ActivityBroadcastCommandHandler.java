package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class ActivityBroadcastCommandHandler implements ICommandHandler {

    private ConnectionStateService rConnectionStateService;

    public ActivityBroadcastCommandHandler(ConnectionStateService rConnectionStateService) {
        this.rConnectionStateService = rConnectionStateService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            ActivityBroadcastCommand activityBroadcast = (ActivityBroadcastCommand)command;
            rConnectionStateService.broadcastToAll(activityBroadcast, conn);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
