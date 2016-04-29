package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

public interface IConnectionManager {
    void addServerConnection(Connection conn);
    void addClientConnection(Connection conn);
    void addConnection(Connection conn);
    void removeConnection(Connection conn);
}
