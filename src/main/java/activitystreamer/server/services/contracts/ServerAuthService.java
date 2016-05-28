package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

public interface ServerAuthService {
  boolean authenticate(Connection conn, String secret);

  boolean isAuthenticated(Connection conn);

  void logout(Connection conn);
}
