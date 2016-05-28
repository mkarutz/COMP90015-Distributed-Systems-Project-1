package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LoginFailedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;

public class LoginFailedCommandHandler implements ICommandHandler {
  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof LoginFailedCommand) {
      conn.close();
      return true;
    }
    return false;
  }
}
