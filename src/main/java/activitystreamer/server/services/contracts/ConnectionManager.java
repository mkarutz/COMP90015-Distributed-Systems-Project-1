package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;
import activitystreamer.core.shared.DisconnectHandler;

public interface ConnectionManager extends DisconnectHandler {
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

    boolean hasParent();
    Connection getParentConnection();
    void setParentConnection(Connection connection);

    boolean isLegacyServer(Connection connection);
    boolean isParentConnection(Connection connection);
    boolean isClientConnection(Connection connection);
    boolean isServerConnection(Connection connection);
    boolean isPendingConnection(Connection connection);
}
