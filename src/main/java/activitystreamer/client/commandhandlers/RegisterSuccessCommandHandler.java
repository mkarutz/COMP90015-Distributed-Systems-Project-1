package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LoginCommand;
import activitystreamer.core.command.RegisterSuccessCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;

public class RegisterSuccessCommandHandler implements ICommandHandler {
  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof RegisterSuccessCommand) {
      Command cmd = new LoginCommand(Settings.getUsername(), Settings.getSecret());
      conn.pushCommand(cmd);
      return true;
    }
    return false;
  }
}
