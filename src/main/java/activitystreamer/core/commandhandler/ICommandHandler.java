package activitystreamer.core.commandhandler;

import java.util.List;

public interface ICommandHandler {
    public boolean handleCommand(ICommand command, Connection conn);
}
