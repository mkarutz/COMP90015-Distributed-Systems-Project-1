package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LockAllowedCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockAllowedCommandHandler implements ICommandHandler {
  private Logger log = LogManager.getLogger();

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    return command instanceof LockAllowedCommand;
  }
}
