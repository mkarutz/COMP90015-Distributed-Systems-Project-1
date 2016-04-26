package activitystreamer.core.commandhandler;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.Connection;
import java.util.List;

public interface ICommandHandler {
    public boolean handleCommandIncoming(ICommand command, Connection conn);
    public boolean handleCommandOutgoing(ICommand command, Connection conn);
}
