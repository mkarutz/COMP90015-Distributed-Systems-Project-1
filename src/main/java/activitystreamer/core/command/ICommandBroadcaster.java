package activitystreamer.core.command;

import activitystreamer.core.shared.Connection;

public interface ICommandBroadcaster {
    void broadcast(ICommand command, Connection exclude);
    void broadcast(ICommand command);
}
