package activitystreamer.server.services;

import activitystreamer.core.shared.Connection;

public interface IUserAuthService {
    String ANONYMOUS = "anonymous";

    enum LockRequestResult {
        SUCCESS,
        FAIL_ALREADY_REGISTERED,
        FAIL_USERNAME_TAKEN
    }

    boolean register(String username, String secret, Connection replyConnection);
    boolean login(Connection conn, String username, String secret);
    void loginAsAnonymous(Connection conn);
    LockRequestResult lockRequest(String username, String secret);
    void lockAllowed(String username, String secret, String serverId);
    void lockDenied(String username, String secret);
    boolean isUserRegistered(String username, String secret);
    boolean isLoggedIn(Connection conn);
    boolean authorise(Connection conn, String username, String secret);
}
