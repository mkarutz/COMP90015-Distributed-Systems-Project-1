package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

public interface IUserAuthService {
    String ANONYMOUS = "anonymous";

    boolean register(String username, String secret, Connection replyConnection);
    boolean login(Connection conn, String username, String secret);
    void logout(Connection conn);
    void loginAsAnonymous(Connection conn);
    void lockRequest(String username, String secret);
    void lockAllowed(String username, String secret, String serverId);
    void lockDenied(String username, String secret);
    boolean isUserRegistered(String username, String secret);
    boolean isLoggedIn(Connection conn);
    boolean authorise(Connection conn, String username, String secret);
}
