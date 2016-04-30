package activitystreamer.core.commandhandler;

import activitystreamer.core.command.Command;
import activitystreamer.core.shared.Connection;

public interface ICommandHandler {
    boolean handleCommand(Command command, Connection conn);
}
