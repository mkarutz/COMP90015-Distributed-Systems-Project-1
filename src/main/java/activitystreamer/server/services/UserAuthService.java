package activitystreamer.server.services;

import java.util.*;

import activitystreamer.core.shared.Connection;
import activitystreamer.core.command.*;

public class UserAuthService {
    public static final String ANONYMOUS = "anonymous";

    private Map<Connection, String> loggedInUsers;
    private Map<String, String> userMap;
    private Map<String, LockRequest> pendingLockRequests;
    private final RemoteServerStateService rServerService;

    public UserAuthService(RemoteServerStateService rServerService) {
        this.rServerService = rServerService;
        this.userMap = new HashMap<>();
        this.pendingLockRequests = new HashMap<>();
        this.loggedInUsers = new HashMap<>();
    }

    public synchronized boolean isUserRegistered(String username, String secret) {
        return !(username == null || secret == null) && secret.equals(userMap.get(username));
    }

    public synchronized boolean isLoggedIn(Connection conn) {
        return loggedInUsers.containsKey(conn);
    }

    public synchronized boolean authorise(Connection conn, String username, String secret) {
        return isLoggedIn(conn) && username != null
                && (ANONYMOUS.equals(username)
                || secret != null && secret.equals(userMap.get(loggedInUsers.get(conn))));
    }

    public synchronized boolean login(Connection conn, String username, String secret) {
        if (!isUserRegistered(username, secret)) {
            return false;
        }
        loggedInUsers.put(conn, username);
        return true;
    }

    public synchronized void loginAsAnonymous(Connection conn) {
        loggedInUsers.put(conn, ANONYMOUS);
    }

    public synchronized boolean register(String username, String secret, Connection replyConnection) {
        if (userMap.containsKey(username) || pendingLockRequests.containsKey(username)) {
            return false;
        }

        sendLockRequest(username, secret, replyConnection);
        return true;
    }

    private void sendLockRequest(String username, String secret, Connection replyConnection) {
        synchronized (rServerService) {
            List<String> knownServerIds = rServerService.getKnownServerIds();
            Set<String> waitingServers = new HashSet<>();
            for (String serverId: knownServerIds) {
                waitingServers.add(serverId);
            }

            LockRequest req = new LockRequest(secret, waitingServers, replyConnection);
            pendingLockRequests.put(username, req);
        }
    }

    public synchronized void lockAllowed(String username, String secret, String serverId) {
        if (pendingLockRequests.containsKey(username)) {
            LockRequest lockRequest = pendingLockRequests.get(username);
            boolean allowed = pendingLockRequests.get(username).lockAllow(serverId);

            if (allowed) {
                LockRequest req = pendingLockRequests.get(username);
                pendingLockRequests.remove(username);

                userMap.put(username, secret);

                ICommand cmd = new RegisterSuccessCommand("Username " + username + " successfully registered");
                req.getReplyConnection().pushCommand(cmd);
            }
        }
    }

    public synchronized LockRequestResult lockRequest(String username, String secret) {
        if (userMap.containsKey(username)) {
            if (!userMap.get(username).equals(secret)) {
                return LockRequestResult.FAIL_USERNAME_TAKEN;
            } else {
                return LockRequestResult.FAIL_ALREADY_REGISTERED;
            }
        } else {
            userMap.put(username, secret);
            return LockRequestResult.SUCCESS;
        }
    }

    public synchronized void lockDenied(String username, String secret) {
        // Check if locally instigated, because we need to notify local client
        // that originally sent the request
        if (pendingLockRequests.containsKey(username)) {
            LockRequest req = pendingLockRequests.get(username);
            pendingLockRequests.remove(username);

            ICommand cmd = new RegisterFailedCommand("Username " + username + " already registered");
            req.getReplyConnection().pushCommand(cmd);
        }

        if (this.userMap.containsKey(username) && this.userMap.get(username) == secret) {
            userMap.remove(username);
        }
    }

    public enum LockRequestResult {
        SUCCESS,
        FAIL_ALREADY_REGISTERED,
        FAIL_USERNAME_TAKEN
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
