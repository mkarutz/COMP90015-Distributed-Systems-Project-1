package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

public interface ConnectionManager {
    void addServerConnection(Connection conn);
    void addClientConnection(Connection conn);
    void addConnection(Connection conn);
    void closeConnection(Connection conn);
    void closeAll();
    int getLoad();

    void eachServerConnection(ConnectionCallback callback);
    interface ConnectionCallback {
        void execute(Connection connection);
    }
}
