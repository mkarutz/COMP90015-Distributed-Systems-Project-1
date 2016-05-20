package activitystreamer.server.services.impl;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConcreteUserAuthService implements UserAuthService {
    private final RemoteServerStateService serverStateService;
    private final ConnectionManager connectionManager;
    private final BroadcastService broadcastService;

    private Map<Connection, String> loggedInUsers;
    private Map<String, String> userMap;
    private Map<String, LockRequest> pendingLockRequests;

    @Inject
    public ConcreteUserAuthService(RemoteServerStateService serverStateService,
                                   ConnectionManager connectionManager,
                                   BroadcastService broadcastService) {
        this.serverStateService = serverStateService;
        this.connectionManager = connectionManager;
        this.broadcastService = broadcastService;

        this.userMap = new HashMap<>();
        this.pendingLockRequests = new HashMap<>();
        this.loggedInUsers = new HashMap<>();
    }

    @Override
    public synchronized boolean login(Connection conn, String username, String secret) {
        if (!isUserRegistered(username, secret)) {
            return false;
        }
        loggedInUsers.put(conn, username);
        connectionManager.addClientConnection(conn);
        return true;
    }

    @Override
    public void logout(Connection conn) {
        loggedInUsers.remove(conn);
    }

    @Override
    public synchronized void loginAsAnonymous(Connection conn) {
        loggedInUsers.put(conn, ANONYMOUS);
        connectionManager.addClientConnection(conn);
    }

    @Override
    public synchronized boolean register(String username, String secret, Connection replyConnection) {
        if (userMap.containsKey(username) || pendingLockRequests.containsKey(username)) {
            return false;
        }

        sendLockRequests(username, secret, replyConnection);
        return true;
    }

    private void sendLockRequests(String username, String secret, Connection replyConnection) {
        Set<String> waitingServers = new HashSet<>();
        Set<String> knownServerIds = serverStateService.getKnownServerIds();
        for (String serverId: knownServerIds) {
            waitingServers.add(serverId);
        }

        if (waitingServers.isEmpty()) {
            registerUser(username, secret, replyConnection);
        }
        LockRequest req = new LockRequest(secret, waitingServers, replyConnection);
        pendingLockRequests.put(username, req);
        broadcastService.broadcastToServers(new LockRequestCommand(username, secret), null);
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
        return isLoggedIn(conn) && (ANONYMOUS.equals(username) || username.equals(loggedInUsers.get(conn)) && secret.equals(userMap.get(username)));
    }

    @Override
    public synchronized void lockAllowed(String username, String secret, String serverId) {
        if (pendingLockRequests.containsKey(username)) {
            LockRequest req = pendingLockRequests.get(username);

            if (req.getSecret().equals(secret)) {
                boolean allowed = req.lockAllow(serverId);

                if (allowed) {
                    pendingLockRequests.remove(username);
                    registerUser(username, secret, req.getReplyConnection());
                }
            }
        }
    }

    @Override
    public synchronized void lockRequest(String username, String secret) {
        if (userMap.containsKey(username) || pendingLockRequests.containsKey(username)) {
            broadcastService.broadcastToServers(new LockDeniedCommand(username, secret), null);
        } else {
            userMap.put(username,secret);
            broadcastService.broadcastToServers(new LockAllowedCommand(username, secret, Settings.getId()), null);
        }
    }

    @Override
    public synchronized void lockDenied(String username, String secret) {
        if (pendingLockRequests.containsKey(username)) {
            LockRequest req = pendingLockRequests.get(username);

            if (req.getSecret().equals(secret)) {
                pendingLockRequests.remove(username);
                Command cmd = new RegisterFailedCommand("Username " + username + " already registered");
                req.getReplyConnection().pushCommand(cmd);
            }
        }

        if (this.userMap.containsKey(username) && this.userMap.get(username).equals(secret)) {
            userMap.remove(username);
        }
    }

    private synchronized void registerUser(String username, String secret, Connection replyConnection){
        userMap.put(username, secret);
        Command cmd = new RegisterSuccessCommand("Username " + username + " successfully registered");
        replyConnection.pushCommand(cmd);
    }

    private static class LockRequest {
        private String secret;
        private Connection replyConnection;
        private Set<String> serverAwaitingIds;

        public LockRequest(String secret, Set<String> serverAwaitingIds, Connection replyConnection) {
            this.secret = secret;
            this.replyConnection = replyConnection;
            this.serverAwaitingIds = serverAwaitingIds;
        }

        public String getSecret() {
            return secret;
        }

        public boolean lockAllow(String serverId) {
            serverAwaitingIds.remove(serverId);
            return serverAwaitingIds.isEmpty();
        }

        public Connection getReplyConnection() {
            return this.replyConnection;
        }
    }
}
