package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidMessageCommandHandler implements ICommandHandler {
  private Logger log = LogManager.getLogger();

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof InvalidMessageCommand) {
      InvalidMessageCommand cmd = (InvalidMessageCommand) command;
      log.error("Invalid message was sent: " + cmd.getInfo());
      conn.close();
      return true;
    } else {
      return false;
    }
  }
}
