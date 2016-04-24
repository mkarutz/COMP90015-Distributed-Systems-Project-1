package activitystreamer.core.command;

import activitystreamer.server.Connection;

public interface ICommandBroadcaster {
    public void broadcastToAll(ICommand command, Connection exclude);
    public void broadcastToServers(ICommand command, Connection exclude);
    public void broadcastToClients(ICommand command, Connection exclude);
}
