package activitystreamer.core.commandprocessor;

import activitystreamer.core.command.Command;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandProcessor {
  protected List<ICommandHandler> handlers = new ArrayList<>();

  public CommandProcessor() {
  }

  public synchronized void processCommandIncoming(Connection connection, Command command) {
    boolean handled = false;
    for (ICommandHandler h : handlers) {
      if (h.handleCommand(command, connection)) {
        handled = true;
        break;
      }
    }

    if (!handled) {
      invalidMessage(connection, command);
    }
  }

  public abstract void invalidMessage(Connection connection, Command command);
}
