package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.client.services.*;

public class ActivityBroadcastCommandHandler implements ICommandHandler {
    ClientReflectionService rClientRefService;

    public ActivityBroadcastCommandHandler(ClientReflectionService rClientRefService) {
        this.rClientRefService = rClientRefService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            ActivityBroadcastCommand activityBroadcast = (ActivityBroadcastCommand)command;

            // Received activity message
            rClientRefService.pushActivityJSON(activityBroadcast.getActivity());

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
