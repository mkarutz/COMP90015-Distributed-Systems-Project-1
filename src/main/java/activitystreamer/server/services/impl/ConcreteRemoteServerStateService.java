
package activitystreamer.server.services.impl;

import activitystreamer.core.command.RedirectCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.util.Settings;

import java.net.InetAddress;
import java.util.*;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcreteRemoteServerStateService implements RemoteServerStateService, ConnectionManager.ConnectionCallback {
  Logger log = LogManager.getLogger();

  private final Map<Connection, State> leastLoadedSecureServer = new HashMap<>();
  private final Map<Connection, State> leastLoadedInsecureServer = new HashMap<>();

  private final ConnectionManager connectionManager;

  @Inject
  public ConcreteRemoteServerStateService(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override
  public synchronized void updateState(String id, int load, InetAddress hostname, int port,
                                       int secureLoad, InetAddress secureHostname, int securePort,
                                       Connection connection) {
    if (hostname != null) {
      leastLoadedInsecureServer.put(connection, new State(load, hostname, port));
    }

    if (secureHostname != null) {
      leastLoadedSecureServer.put(connection, new State(secureLoad, secureHostname, securePort));
    }
  }

  private void printTable() {
    System.out.println("\n\n\n");
    System.out.println("LOAD = " + connectionManager.getLoad());

    System.out.println("Secure Server States");
    System.out.println("+------+----+----+");
    System.out.printf("|%-6s|%4s|%4s|\n", "id", "load", "port");
    System.out.println("+------+----+----+");
    for (Map.Entry<Connection, State> entry : leastLoadedSecureServer.entrySet()) {
      System.out.printf("|%-6s|%4s|%4s|\n", entry.getKey().getSocket().getPort(), entry.getValue().getLoad(), entry.getValue().getPort());
      System.out.println("+------+----+----+");
    }

    System.out.println("\nInsecure Server States");
    System.out.println("+------+----+----+");
    System.out.printf("|%-6s|%4s|%4s|\n", "id", "load", "port");
    System.out.println("+------+----+----+");
    for (Map.Entry<Connection, State> entry : leastLoadedInsecureServer.entrySet()) {
      System.out.printf("|%-6s|%4s|%4s|\n", entry.getKey().getSocket().getPort(), entry.getValue().getLoad(), entry.getValue().getPort());
      System.out.println("+------+----+----+");
    }
    System.out.println("\n\n");
  }

  @Override
  public synchronized void announce() {
    printTable();
    connectionManager.eachServerConnection(this);
  }

  @Override
  public void execute(Connection connection) {
    State secureBackup = leastLoadedSecureServer.remove(connection);
    State insecureBackup = leastLoadedInsecureServer.remove(connection);

    State minSecure = leastLoadedSecure();
    State minInsecure = leastLoadedInsecure();

    int load = connectionManager.getLoad();

    State secure = minSecure != null && minSecure.getLoad() < load
        ? minSecure
        : new State(load, connection.getSocket().getLocalAddress(), Settings.getSecureLocalPort());

    State insecure = minInsecure != null && minInsecure.getLoad() < load
        ? minInsecure
        : new State(load, connection.getSocket().getLocalAddress(), Settings.getLocalPort());

    connection.pushCommand(new ServerAnnounceCommand(
        Settings.getId(),
        insecure.getLoad(),
        insecure.getHostname(),
        insecure.getPort(),
        secure.getLoad(),
        secure.getHostname(),
        secure.getPort()
    ));

    if (secureBackup != null) {
      leastLoadedSecureServer.put(connection, secureBackup);
    }

    if (insecureBackup != null) {
      leastLoadedInsecureServer.put(connection, insecureBackup);
    }
  }

  private State leastLoadedSecure() {
    return leastLoadedSecureServer.isEmpty() ? null : Collections.min(leastLoadedSecureServer.values());
  }

  private State leastLoadedInsecure() {
    return leastLoadedInsecureServer.isEmpty() ? null : Collections.min(leastLoadedInsecureServer.values());
  }

  @Override
  public synchronized boolean loadBalance(Connection connection) {
    State redirectTo;
    if (connection.getSocket().getLocalPort() == Settings.getSecureLocalPort()) {
      if ((redirectTo = getSecureServerToRedirectTo()) != null) {
        log.debug("Redirecting Securely!!!!!!!!!!!!!!!");
        connection.pushCommand(new RedirectCommand(redirectTo.getHostname(), redirectTo.getPort()));
        connectionManager.closeConnection(connection);
        return true;
      }
    } else {
      if ((redirectTo = getInsecureServerToRedirectTo()) != null) {
        log.debug("Redirecting Insecurely!!!!!!!!!!!!!!!");
        connection.pushCommand(new RedirectCommand(redirectTo.getHostname(), redirectTo.getPort()));
        connectionManager.closeConnection(connection);
        return true;
      }
    }
    return false;
  }

  private State getSecureServerToRedirectTo() {
    State state = leastLoadedSecure();
    if (state != null && state.getLoad() < connectionManager.getLoad()) {
      return state;
    }

    return null;
  }

  private State getInsecureServerToRedirectTo() {
    State state = leastLoadedInsecure();
    if (state != null && state.getLoad() < connectionManager.getLoad()) {
      return state;
    }


    return null;
  }

  private static class State implements Comparable<State> {
    private final int load;
    private final InetAddress hostname;
    private final int port;

    public State(int load, InetAddress hostname, int port) {
      this.load = load;
      this.hostname = hostname;
      this.port = port;
    }

    @Override
    public int compareTo(State state) {
      return load - state.load;
    }

    public int getLoad() {
      return load;
    }

    public InetAddress getHostname() {
      return hostname;
    }

    public int getPort() {
      return port;
    }
  }
}
