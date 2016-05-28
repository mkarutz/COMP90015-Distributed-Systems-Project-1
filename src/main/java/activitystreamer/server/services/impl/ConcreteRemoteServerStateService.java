package activitystreamer.server.services.impl;

import activitystreamer.core.command.RedirectCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.util.*;

public class ConcreteRemoteServerStateService implements RemoteServerStateService, ConnectionManager.ConnectionCallback {
  private final Map<String, State> leastLoadedSecureServer = new HashMap<>();
  private final Map<String, State> leastLoadedInsecureServer = new HashMap<>();
  private final Map<Connection, Set<String>> idsOnConnection = new HashMap<>();
  private final ConnectionManager connectionManager;
  Logger log = LogManager.getLogger();

  @Inject
  public ConcreteRemoteServerStateService(ConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override
  public synchronized void updateState(String id, int load, InetAddress hostname, int port,
                                       int secureLoad, InetAddress secureHostname, int securePort,
                                       Connection connection) {
    addID(connection, id);

    if (hostname != null) {
      leastLoadedInsecureServer.put(id, new State(load, hostname, port));
    }

    if (secureHostname != null) {
      leastLoadedSecureServer.put(id, new State(secureLoad, secureHostname, securePort));
    }
  }

  private void addID(Connection c, String id) {
    if (!idsOnConnection.containsKey(c)) {
      idsOnConnection.put(c, new HashSet<String>());
    }

    idsOnConnection.get(c).add(id);
  }

  private void printTable() {
    System.out.println("\n\n\n");
    System.out.println("LOAD = " + connectionManager.getLoad());

    System.out.println("Secure Server States");
    System.out.println("+--------------------------------+----+----+");
    System.out.printf("|%-32s|%4s|%4s|\n", "id", "load", "port");
    System.out.println("+--------------------------------+----+----+");
    for (Map.Entry<String, State> entry : leastLoadedSecureServer.entrySet()) {
      System.out.printf("|%-32s|%4s|%4s|\n", entry.getKey(), entry.getValue().getLoad(), entry.getValue().getPort());
      System.out.println("+--------------------------------+----+----+");
    }

    System.out.println("\nInsecure Server States");
    System.out.println("+--------------------------------+----+----+");
    System.out.printf("|%-32s|%4s|%4s|\n", "id", "load", "port");
    System.out.println("+--------------------------------+----+----+");
    for (Map.Entry<String, State> entry : leastLoadedInsecureServer.entrySet()) {
      System.out.printf("|%-32s|%4s|%4s|\n", entry.getKey(), entry.getValue().getLoad(), entry.getValue().getPort());
      System.out.println("+--------------------------------+----+----+");
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
    Map<String, State> secureBackup = new HashMap<>();
    Map<String, State> insecureBackup = new HashMap<>();

    if (idsOnConnection.containsKey(connection)) {
      for (String id : idsOnConnection.get(connection)) {
        if (leastLoadedSecureServer.containsKey(id)) {
          secureBackup.put(id, leastLoadedSecureServer.remove(id));
        }
        if (leastLoadedInsecureServer.containsKey(id)) {
          insecureBackup.put(id, leastLoadedInsecureServer.remove(id));
        }
      }
    }

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

    for (Map.Entry<String, State> entry : secureBackup.entrySet()) {
      leastLoadedSecureServer.put(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, State> entry : insecureBackup.entrySet()) {
      leastLoadedInsecureServer.put(entry.getKey(), entry.getValue());
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
