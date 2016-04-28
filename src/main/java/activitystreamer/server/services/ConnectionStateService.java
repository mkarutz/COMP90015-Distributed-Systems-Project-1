
package activitystreamer.server.services;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;

public class ConnectionStateService {
    private HashMap<Connection, ConnectionType> connectionStates;

    public enum ConnectionType {
        UNKNOWN,
        SERVER,
        CLIENT
    }

    public ConnectionStateService() {
        this.connectionStates = new HashMap<Connection, ConnectionType>();
    }

    public synchronized void registerConnection(Connection conn) {
        this.connectionStates.put(conn, ConnectionType.UNKNOWN);
    }

    public synchronized void setConnectionType(Connection conn, ConnectionType type) {
        this.connectionStates.put(conn, type);
    }

    public ConnectionType getConnectionType(Connection conn) {
        if (this.connectionStates.containsKey(conn)) {
            return this.connectionStates.get(conn);
        }
        return ConnectionType.UNKNOWN;
    }

    public void broadcastToAll(ICommand command, Connection exclude) {
        for (Map.Entry<Connection, ConnectionType> entry : connectionStates.entrySet()) {
            if (entry.getKey() != exclude) {
                entry.getKey().pushCommand(command);
            }
        }
    }

    public void broadcastToServers(ICommand command, Connection exclude) {
        for (Map.Entry<Connection, ConnectionType> entry : connectionStates.entrySet()) {
            if (entry.getKey() != exclude && entry.getValue() == ConnectionType.SERVER) {
                entry.getKey().pushCommand(command);
            }
        }
    }
}
