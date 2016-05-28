package activitystreamer.client.commandhandlers;

import activitystreamer.client.services.ClientReflectionService;
import activitystreamer.core.command.ActivityBroadcastCommand;
import activitystreamer.core.command.Command;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;

public class ActivityBroadcastCommandHandler implements ICommandHandler {
  ClientReflectionService rClientRefService;

  public ActivityBroadcastCommandHandler(ClientReflectionService rClientRefService) {
    this.rClientRefService = rClientRefService;
  }

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof ActivityBroadcastCommand) {
      ActivityBroadcastCommand activityBroadcast = (ActivityBroadcastCommand) command;

      // Received activity message
      rClientRefService.pushActivityJSON(activityBroadcast.getActivity());

      return true;
    } else {
      return false;
    }
  }
}
