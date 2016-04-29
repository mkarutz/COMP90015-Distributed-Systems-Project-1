package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

public interface IServerAuthService {
    boolean authenticate(Connection conn, String secret);
    boolean isAuthenticated(Connection conn);
}
