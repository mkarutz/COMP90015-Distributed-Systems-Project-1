package activitystreamer.core.commandhandler;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.Connection;
import java.util.List;

public interface ICommandHandler {
    boolean handleCommandIncoming(ICommand command, Connection conn);
    boolean handleCommandOutgoing(ICommand command, Connection conn);
}
