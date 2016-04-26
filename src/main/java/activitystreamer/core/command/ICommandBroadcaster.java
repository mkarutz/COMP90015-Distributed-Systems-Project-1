package activitystreamer.core.command;

import activitystreamer.core.shared.Connection;

public interface ICommandBroadcaster {
    public void broadcast(ICommand command, Connection exclude);
}
