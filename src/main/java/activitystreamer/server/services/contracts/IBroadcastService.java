package activitystreamer.server.services.contracts;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.Connection;

public interface IBroadcastService {
    void broadcastToServers(ICommand command);
    void broadcastToClients(ICommand command);
    void broadcastToAll(ICommand command);
    void broadcastToServers(ICommand command, Connection exclude);
    void broadcastToClients(ICommand command, Connection exclude);
    void broadcastToAll(ICommand command, Connection exclude);
}
