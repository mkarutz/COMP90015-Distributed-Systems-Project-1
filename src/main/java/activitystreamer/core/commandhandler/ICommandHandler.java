package activitystreamer.core.commandhandler;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.Connection;
import java.util.List;

public interface ICommandHandler {
    public boolean handleCommand(ICommand command, Connection conn);
}
