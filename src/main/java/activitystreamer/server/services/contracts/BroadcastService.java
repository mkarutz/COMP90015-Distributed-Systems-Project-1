package activitystreamer.server.services.contracts;

import activitystreamer.core.command.Command;
import activitystreamer.core.shared.Connection;

import java.util.Set;

public interface BroadcastService {
  void broadcastToServers(Command command);

  void broadcastToClients(Command command);

  void broadcastToAll(Command command);

  void broadcastToServersExcludingMany(Command command, Set<Connection> exclude);

  void broadcastToServers(Command command, Connection exclude);

  void broadcastToClients(Command command, Connection exclude);

  void broadcastToAll(Command command, Connection exclude);
}
