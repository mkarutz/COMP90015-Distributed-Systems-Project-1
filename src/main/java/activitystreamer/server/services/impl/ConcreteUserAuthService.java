package activitystreamer.server.services.impl;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConcreteUserAuthService implements UserAuthService {
  private final ConnectionManager connectionManager;
  private final BroadcastService broadcastService;

  private final Map<Connection, String> loggedInUsers;
  private final Map<String, String> userMap;
  private final Map<String, PendingRegistrationRequest> pendingRegistrationRequestMap;
  private final Map<UsernameSecretPair, Set<Connection>> pendingLogins;

  private final Map<UsernameSecretPair, Connection> originator;

  @Inject
  public ConcreteUserAuthService(ConnectionManager connectionManager,
                                 BroadcastService broadcastService) {
    this.connectionManager = connectionManager;
    this.broadcastService = broadcastService;

    this.userMap = new HashMap<>();
    this.loggedInUsers = new HashMap<>();
    this.pendingRegistrationRequestMap = new HashMap<>();
    this.pendingLogins = new HashMap<>();
    this.originator = new HashMap<>();
  }

  @Override
  public Connection getOriginator(String username, String secret) {
    return originator.get(new UsernameSecretPair(username, secret));
  }

  @Override
  public synchronized void login(String username, String secret, Connection conn) {
    if (!userMap.containsKey(username)) {
      if (connectionManager.hasParent()) {
        addPendingLogin(username, secret, conn);
        connectionManager.getParentConnection().pushCommand(new LoginCommand(username, secret));
      } else {
        conn.pushCommand(new LoginFailedCommand("Login failed.", username, secret));
      }
      return;
    }

    if (userMap.get(username).equals(secret)) {
      if (connectionManager.isPendingConnection(conn)) {
        loginConnection(conn, username);
      }
      conn.pushCommand(new LoginSuccessCommand("Login successful.", username, secret));
      return;
    }

    conn.pushCommand(new LoginFailedCommand("Login failed.", username, secret));
  }

  private void addPendingLogin(String username, String secret, Connection conn) {
    UsernameSecretPair key = new UsernameSecretPair(username, secret);
    if (!pendingLogins.containsKey(key)) {
      pendingLogins.put(key, new HashSet<Connection>());
    }
    pendingLogins.get(key).add(conn);
  }

  @Override
  public void loginFailed(String username, String secret) {
    UsernameSecretPair key = new UsernameSecretPair(username, secret);
    if (pendingLogins.containsKey(key)) {
      Set<Connection> conns = pendingLogins.get(key);
      for (Connection conn : conns) {
        conn.pushCommand(new LoginFailedCommand("Login failed.", username, secret));
      }
    }
  }

  @Override
  public void loginSuccess(String username, String secret) {
    UsernameSecretPair key = new UsernameSecretPair(username, secret);
    userMap.put(username, secret);
    if (pendingLogins.containsKey(key)) {
      Set<Connection> conns = pendingLogins.get(key);
      for (Connection conn : conns) {
        if (connectionManager.isPendingConnection(conn)) {
          loginConnection(conn, username);
        }
        conn.pushCommand(new LoginSuccessCommand("Login success.", username, secret));
      }
    }
  }

  @Override
  public void logout(Connection conn) {
    loggedInUsers.remove(conn);
  }

  @Override
  public synchronized void loginAsAnonymous(Connection conn) {
    loginConnection(conn, ANONYMOUS);
    conn.pushCommand(new LoginSuccessCommand("Logged in as anonymous", ANONYMOUS, null));
  }

  @Override
  public synchronized boolean register(String username, String secret, Connection replyConnection) {
    if (userMap.containsKey(username) || pendingRegistrationRequestMap.containsKey(username)) {
      return false;
    }

    if (connectionManager.hasParent()) {
      /*
      START OF DIRTY HACK
       */
      if (connectionManager.isLegacyServer(replyConnection)) {
        originator.put(new UsernameSecretPair(username, secret), replyConnection);
      }
      /*
      END OF DIRTY HACK
       */
      pendingRegistrationRequestMap.put(
          username,
          new PendingRegistrationRequest(secret, replyConnection, connectionManager.getParentConnection())
      );
      connectionManager.getParentConnection().pushCommand(new RegisterCommand(username, secret));
    } else {
      // This is a dirty hack to sync registrations with legacy servers
      if (connectionManager.isLegacyServer(replyConnection)) {
        broadcastService.broadcastToServers(new LockRequestCommand(username, secret, Settings.getId()), replyConnection);
      } else {
        broadcastService.broadcastToServers(new LockRequestCommand(username, secret, Settings.getId()));
      }

      registerUser(username, secret, replyConnection);
    }

    return true;
  }

  @Override
  public synchronized boolean isUserRegistered(String username, String secret) {
    return !(username == null || secret == null) && secret.equals(userMap.get(username));
  }

  @Override
  public synchronized boolean isLoggedIn(Connection conn) {
    return loggedInUsers.containsKey(conn);
  }

  @Override
  public synchronized boolean authorise(Connection conn, String username, String secret) {
    return isLoggedIn(conn) && (ANONYMOUS.equals(username)
        || username.equals(loggedInUsers.get(conn)) && secret.equals(userMap.get(username)));
  }

  @Override
  public void registerSuccess(String username, String secret) {
    if (pendingRegistrationRequestMap.containsKey(username)) {
      System.out.println("CONTAINS");
      PendingRegistrationRequest registrationRequest = pendingRegistrationRequestMap.get(username);
      if (registrationRequest.getSecret().equals(secret)) {
        System.out.println("EQUALS");
        registerUser(username, secret, registrationRequest.getDownstreamConnection());
      }
    }
  }

  @Override
  public void registerFailed(String username, String secret) {
    if (pendingRegistrationRequestMap.containsKey(username)) {
      PendingRegistrationRequest registrationRequest = pendingRegistrationRequestMap.remove(username);
      Connection downstreamConnection = registrationRequest.getDownstreamConnection();

      if (!registrationRequest.getSecret().equals(secret)) {
        return;
      }

      if (connectionManager.isLegacyServer(downstreamConnection)) {
        downstreamConnection.pushCommand(new LockDeniedCommand(username, secret));
      } else {
        Command cmd = new RegisterFailedCommand("Register failed", username, secret);
        downstreamConnection.pushCommand(cmd);
        if (connectionManager.isClientConnection(downstreamConnection)) {
          connectionManager.closeConnection(downstreamConnection);
        }
      }
    }
  }

  private void loginConnection(Connection conn, String username) {
    loggedInUsers.put(conn, username);
    connectionManager.addClientConnection(conn);
  }

  private synchronized void registerUser(String username, String secret, Connection replyConnection) {
    userMap.put(username, secret);
    if (replyConnection == null) {
      return;
    }

    if (connectionManager.isLegacyServer(replyConnection)) {
      replyConnection.pushCommand(new LockAllowedCommand(username, secret, Settings.getId()));
    } else {
      Command cmd = new RegisterSuccessCommand("Username " + username + " successfully registered", username, secret);
      replyConnection.pushCommand(cmd);
    }
  }

  @Override
  public boolean usernameExists(String username) {
    return userMap.containsKey(username);
  }

  @Override
  public String getSecret(String username) {
    return userMap.get(username);
  }

  @Override
  public void put(String username, String secret) {
    userMap.put(username, secret);
  }

  private static class PendingRegistrationRequest {
    private String secret;
    private Connection downstreamConnection;
    private Connection upstreamConnection;

    public PendingRegistrationRequest(String secret, Connection downstreamConnection, Connection upstreamConnection) {
      this.secret = secret;
      this.downstreamConnection = downstreamConnection;
      this.upstreamConnection = upstreamConnection;
    }

    public String getSecret() {
      return secret;
    }

    public Connection getDownstreamConnection() {
      return downstreamConnection;
    }

    public Connection getUpstreamConnection() {
      return upstreamConnection;
    }
  }

  private static class UsernameSecretPair {
    private final String username;
    private final String secret;

    public UsernameSecretPair(String username, String secret) {
      if (username == null || secret == null) {
        throw new IllegalArgumentException("Null");
      }
      this.username = username;
      this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      UsernameSecretPair that = (UsernameSecretPair) o;

      if (!username.equals(that.username)) return false;
      return secret.equals(that.secret);

    }

    @Override
    public int hashCode() {
      int result = username.hashCode();
      result = 31 * result + secret.hashCode();
      return result;
    }

    public String getUsername() {
      return username;
    }

    public String getSecret() {
      return secret;
    }
  }
}
