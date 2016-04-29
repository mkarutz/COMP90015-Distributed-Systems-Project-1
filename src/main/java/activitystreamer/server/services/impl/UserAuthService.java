package activitystreamer.server.services.impl;

import java.util.*;

import activitystreamer.core.shared.Connection;
import activitystreamer.core.command.*;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.util.Settings;

public class UserAuthService implements IUserAuthService {
    private final RemoteServerStateService rServerService;
    private final IBroadcastService rIBroadcastService;
    private Map<Connection, String> loggedInUsers;
    private Map<String, String> userMap;
    private Map<String, LockRequest> pendingLockRequests;

    public UserAuthService(RemoteServerStateService rServerService,
                           IBroadcastService rIBroadcastService) {
        this.rServerService = rServerService;
        this.rIBroadcastService = rIBroadcastService;

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
        return true;
    }

    @Override
    public void logout(Connection conn) {
        loggedInUsers.remove(conn);
    }

    @Override
    public synchronized void loginAsAnonymous(Connection conn) {
        loggedInUsers.put(conn, ANONYMOUS);
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
        synchronized (rServerService) {
            List<String> knownServerIds = rServerService.getKnownServerIds();
            for (String serverId: knownServerIds) {
                waitingServers.add(serverId);
            }
        }

        if (waitingServers.isEmpty()){
            registerUser(username,secret,replyConnection);
        }
        LockRequest req = new LockRequest(secret, waitingServers, replyConnection);
        pendingLockRequests.put(username, req);
        rIBroadcastService.broadcastToServers(new LockRequestCommand(username, secret), null);
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
        return isLoggedIn(conn) && username != null
                && (ANONYMOUS.equals(username)
                || secret != null && secret.equals(userMap.get(loggedInUsers.get(conn))));
    }

    @Override
    public synchronized void lockAllowed(String username, String secret, String serverId) {
        if (pendingLockRequests.containsKey(username)) {
            LockRequest lockRequest = pendingLockRequests.get(username);
            boolean allowed = pendingLockRequests.get(username).lockAllow(serverId);

            if (allowed) {
                LockRequest req = pendingLockRequests.get(username);
                pendingLockRequests.remove(username);

                registerUser(username,secret,req.getReplyConnection());
            }
        }
    }

    @Override
    public synchronized void lockRequest(String username, String secret) {
        if (userMap.containsKey(username)) {
            rIBroadcastService.broadcastToServers(new LockDeniedCommand(username, secret), null);
        } else {
            rIBroadcastService.broadcastToServers(new LockAllowedCommand(username, secret, Settings.getId()), null);
        }
    }

    @Override
    public synchronized void lockDenied(String username, String secret) {
        // Check if locally instigated, because we need to notify local client
        // that originally sent the request
        if (pendingLockRequests.containsKey(username)) {
            LockRequest req = pendingLockRequests.get(username);
            pendingLockRequests.remove(username);

            ICommand cmd = new RegisterFailedCommand("Username " + username + " already registered");
            req.getReplyConnection().pushCommand(cmd);
        }

        if (this.userMap.containsKey(username) && this.userMap.get(username).equals(secret)) {
            userMap.remove(username);
        }
    }

    private synchronized void registerUser(String username,String secret,Connection replyConnection){
        userMap.put(username, secret);

        ICommand cmd = new RegisterSuccessCommand("Username " + username + " successfully registered");
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

        public boolean lockAllow(String serverId) {
            serverAwaitingIds.remove(serverId);
            return serverAwaitingIds.isEmpty();
        }

        public Connection getReplyConnection() {
            return this.replyConnection;
        }
    }
}
