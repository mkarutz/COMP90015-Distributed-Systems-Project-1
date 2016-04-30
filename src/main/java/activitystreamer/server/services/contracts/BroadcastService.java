package activitystreamer.server.services.contracts;

import activitystreamer.core.command.Command;
import activitystreamer.core.shared.Connection;

public interface BroadcastService {
    void broadcastToServers(Command command);
    void broadcastToClients(Command command);
    void broadcastToAll(Command command);
    void broadcastToServers(Command command, Connection exclude);
    void broadcastToClients(Command command, Connection exclude);
    void broadcastToAll(Command command, Connection exclude);
}
