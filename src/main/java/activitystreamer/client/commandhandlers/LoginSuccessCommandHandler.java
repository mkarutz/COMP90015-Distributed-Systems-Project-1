package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LoginSuccessCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;

public class LoginSuccessCommandHandler implements ICommandHandler {
  @Override
  public boolean handleCommand(Command command, Connection conn) {
    return command instanceof LoginSuccessCommand;
  }
}
