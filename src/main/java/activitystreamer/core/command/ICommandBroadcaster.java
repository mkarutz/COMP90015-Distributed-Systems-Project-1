package activitystreamer.core.command;

import activitystreamer.core.shared.Connection;

public interface ICommandBroadcaster {
    public void broadcastToAll(ICommand command, Connection exclude);
    public void broadcastToServers(ICommand command, Connection exclude);
    public void broadcastToClients(ICommand command, Connection exclude);
}
