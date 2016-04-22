package activitystreamer.core.commandhandler;

import activitystreamer.core.command.ICommand;
import activitystreamer.server.Connection;
import java.util.List;

public interface ICommandHandler {
    public boolean handleCommand(ICommand command, Connection conn);
}
