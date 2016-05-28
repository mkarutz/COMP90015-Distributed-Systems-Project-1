package activitystreamer.server.services.impl;

import activitystreamer.core.command.Command;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;
import java.util.HashSet;
import java.util.Set;

public class NetworkManagerService implements BroadcastService, ConnectionManager, ServerAuthService {
  private final Set<Connection> pendingConnections = new HashSet<>();
  private final Set<Connection> serverConnections = new HashSet<>();
  private final Set<Connection> clientConnections = new HashSet<>();
  private final UserAuthService userAuthService;
  private Logger log = LogManager.getLogger();
  private Connection parent;

  @Inject
  public NetworkManagerService(UserAuthService userAuthService) {
    this.userAuthService = userAuthService;
  }

  @Override
  public synchronized void broadcastToServers(Command command) {
    broadcastToServers(command, null);
  }

  @Override
  public synchronized void broadcastToClients(Command command) {
    broadcastToClients(command, null);
  }

  @Override
  public synchronized void broadcastToAll(Command command) {
    broadcastToAll(command, null);
  }

  @Override
  public synchronized void broadcastToServers(Command command, Connection exclude) {
    for (Connection connection : serverConnections) {
      if (connection == exclude) {
        continue;
      }
      connection.pushCommand(command);
    }
  }

  @Override
  public void broadcastToServersExcludingMany(Command command, Set<Connection> exclude) {
    for (Connection connection : serverConnections) {
      if (exclude.contains(connection)) {
        continue;
      }
      connection.pushCommand(command);
    }
  }

  @Override
  public synchronized void broadcastToClients(Command command, Connection exclude) {
    for (Connection connection : clientConnections) {
      if (connection == exclude) {
        continue;
      }
      connection.pushCommand(command);
    }
  }

  @Override
  public synchronized void broadcastToAll(Command command, Connection exclude) {
    log.debug("Broadcasting command: " + command);
    broadcastToServers(command, exclude);
    broadcastToClients(command, exclude);
  }

  @Override
  public synchronized void eachServerConnection(ConnectionCallback callback) {
    for (Connection connection : serverConnections) {
      callback.execute(connection);
    }
  }

  @Override
  public synchronized void addServerConnection(Connection conn) {
    removeConnection(conn);
    serverConnections.add(conn);
  }

  @Override
  public synchronized void addClientConnection(Connection conn) {
    removeConnection(conn);
    clientConnections.add(conn);
    log.debug("Added client connection. Load = " + clientConnections.size());
  }

  @Override
  public synchronized void addConnection(Connection conn) {
    removeConnection(conn);
    pendingConnections.add(conn);
  }

  @Override
  public synchronized void closeConnection(Connection conn) {
    log.debug("################ Closing connection: " + conn);
    removeConnection(conn);
    logoutConnection(conn);
    conn.close();
  }

  private void removeConnection(Connection conn) {
    pendingConnections.remove(conn);
    clientConnections.remove(conn);
    serverConnections.remove(conn);
  }

  private void logoutConnection(Connection conn) {
    userAuthService.logout(conn);
  }

  @Override
  public synchronized void closeAll() {
    for (Connection connection : serverConnections) {
      connection.close();
      userAuthService.logout(connection);
    }
    for (Connection connection : pendingConnections) {
      connection.close();
    }
    for (Connection connection : clientConnections) {
      connection.close();
    }
    pendingConnections.clear();
    serverConnections.clear();
    clientConnections.clear();
  }

  @Override
  public synchronized int getLoad() {
    log.info("Load = " + clientConnections.size());
    return clientConnections.size();
  }

  @Override
  public synchronized void logout(Connection conn) {
    serverConnections.remove(conn);
    closeConnection(conn);
  }

  @Override
  public synchronized boolean authenticate(Connection conn, String secret) {
    if (Settings.getSecret().equals(secret)) {
      addServerConnection(conn);
      return true;
    }
    return false;
  }

  @Override
  public synchronized boolean isAuthenticated(Connection conn) {
    return serverConnections.contains(conn);
  }

  @Override
  public boolean hasParent() {
    log.debug("HAS PARENT: " + (parent != null ? "TRUE" : "FALSE"));
    return parent != null;
  }

  @Override
  public synchronized Connection getParentConnection() {
    return parent;
  }

  @Override
  public synchronized void setParentConnection(Connection connection) {
    parent = connection;
  }

  @Override
  public synchronized boolean isLegacyServer(Connection connection) {
    // TODO test this
    log.debug("IS LEGACY SERVER?");
    log.debug("  serverConnection.contains: " + (serverConnections.contains(connection) ? "TRUE" : "FALSE"));
    log.debug("  instanceof SSLSocket: " + (connection.getSocket() instanceof SSLSocket ? "TRUE" : "FALSE"));
    return serverConnections.contains(connection) && !(connection.getSocket() instanceof SSLSocket);
  }

  @Override
  public boolean isParentConnection(Connection connection) {
    return getParentConnection() == connection;
  }

  @Override
  public boolean isClientConnection(Connection connection) {
    return clientConnections.contains(connection);
  }

  @Override
  public boolean isServerConnection(Connection connection) {
    return serverConnections.contains(connection);
  }

  @Override
  public boolean isPendingConnection(Connection connection) {
    return pendingConnections.contains(connection);
  }
}
