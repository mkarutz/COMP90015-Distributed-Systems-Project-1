package activitystreamer.server.services;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.shared.Connection;
import activitystreamer.core.command.*;

public class UserAuthService {

    // Note, inner class is only used internally, hence private
    private static class LocalLockRequest {
        private String secret;
        private Connection replyConnection;
        private List<String> serverAwaitingIds;

        public LocalLockRequest(String secret, List<String> serverAwaitingIds, Connection replyConnection) {
            this.secret = secret;
            this.replyConnection = replyConnection;
            this.serverAwaitingIds = serverAwaitingIds;
        }

        public boolean registerServerAllow(String serverId) {
            // If server allows a lock request, remove it from awaiting list
            serverAwaitingIds.remove(serverId);

            // If list empty, then lock request is OK
            if (serverAwaitingIds.size() == 0) {
                return true;
            }
            return false;
        }

        public Connection getReplyConnection() {
            return this.replyConnection;
        }
    }

    public enum LockRequestResult {
        SUCCESS,
        FAIL_ALREADY_REGISTERED,
        FAIL_USERNAME_TAKEN
    }

    // Private service trackers/hash maps
    private RemoteServerStateService          rServerService;

    private HashMap<String, String>           userMap;
    private HashMap<String, LocalLockRequest> userLocalLockRequestMap;

    public UserAuthService(RemoteServerStateService rServerService) {
        this.rServerService = rServerService;
        this.userMap = new HashMap<String, String>();
        this.userLocalLockRequestMap = new HashMap<String, LocalLockRequest>();
    }

    public boolean isUserRegistered(String username, String secret) {
        if (userMap.containsKey(username)) {
            if (userMap.get(username) == secret) {
                return true;
            }
        }
        return false;
    }

    // Returns false if user already registered locally, or lock request already
    // instigated locally for that username
    public boolean putLocalLockRequest(String username, String secret, Connection replyConnection) {
        if (userMap.containsKey(username) || userLocalLockRequestMap.containsKey(username)) {
            return false;
        }

        // Retrieve current list of server ids from remote server state service
        // These will be used to track which servers have registered lock allowed messages
        List<String> knownServerIds = rServerService.getKnownServerIds();

        LocalLockRequest req = new LocalLockRequest(secret, knownServerIds, replyConnection);
        userLocalLockRequestMap.put(username, req);
        // Also put normal lock request (?)
        //putLockRequest(username, secret);

        return true;
    }

    public void putLockAllowed(String username, String secret, String serverId) {
        // Only put if locally instigated
        if (userLocalLockRequestMap.containsKey(username)) {
            boolean allowed = userLocalLockRequestMap.get(username).registerServerAllow(serverId);

            if (allowed) {
                // If allowed, this means all servers have allowed the lock request
                // Original user needs to be notified
                LocalLockRequest req = userLocalLockRequestMap.get(username);
                userLocalLockRequestMap.remove(username); // Remove record

                ICommand cmd = new RegisterSuccessCommand("Username " + username + " successfully registered");
                req.getReplyConnection().pushCommand(cmd);
            }
        }
    }

    public LockRequestResult putLockRequest(String username, String secret) {
        if (userMap.containsKey(username)) {
            if (userMap.get(username) != secret) {
                return LockRequestResult.FAIL_USERNAME_TAKEN;
            } else {
                return LockRequestResult.FAIL_ALREADY_REGISTERED;
            }
        } else {
            // Register lock
            userMap.put(username, secret);
            return LockRequestResult.SUCCESS;
        }
    }

    public void putLockDenied(String username, String secret) {
        // Check if locally instigated, because we need to notify local client
        // that originally sent the request
        if (userLocalLockRequestMap.containsKey(username)) {
            LocalLockRequest req = userLocalLockRequestMap.get(username);
            userLocalLockRequestMap.remove(username);

            ICommand cmd = new RegisterFailedCommand("Username " + username + " already registered");
            req.getReplyConnection().pushCommand(cmd);
        }

        if (this.userMap.containsKey(username) && this.userMap.get(username) == secret) {
            userMap.remove(username);
        }
    }
}
