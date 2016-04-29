package activitystreamer.server.services.impl;

import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;

import java.util.HashSet;
import java.util.Set;

public class NetworkManagerService implements BroadcastService, ConnectionManager {
    private Set<Connection> pendingConnections = new HashSet<>();
    private Set<Connection> serverConnections = new HashSet<>();
    private Set<Connection> clientConnections = new HashSet<>();
    
    @Override
    public synchronized void broadcastToServers(ICommand command) {
        broadcastToServers(command, null);
    }

    @Override
    public synchronized void broadcastToClients(ICommand command) {
        broadcastToClients(command, null);
    }

    @Override
    public synchronized void broadcastToAll(ICommand command) {
        broadcastToAll(command, null);
    }

    @Override
    public synchronized void broadcastToServers(ICommand command, Connection exclude) {
        for (Connection connection: serverConnections) {
            if (connection == exclude) { continue; }
            connection.pushCommand(command);
        }
    }

    @Override
    public synchronized void broadcastToClients(ICommand command, Connection exclude) {
        for (Connection connection: serverConnections) {
            if (connection == exclude) { continue; }
            connection.pushCommand(command);
        }
    }

    @Override
    public synchronized void broadcastToAll(ICommand command, Connection exclude) {
        broadcastToServers(command, exclude);
        broadcastToClients(command, exclude);
    }

    @Override
    public synchronized void eachServerConnection(ConnectionCallback callback) {
        for (Connection connection: serverConnections) {
            callback.execute(connection);
        }
    }

    @Override
    public synchronized void addServerConnection(Connection conn) {
        removeConnection(conn);
        serverConnections.add(conn);
    }

    @Override
    public synchronized void addClientConnection(Connection conn) {
        removeConnection(conn);
        clientConnections.add(conn);
    }

    @Override
    public synchronized void addConnection(Connection conn) {
        removeConnection(conn);
        pendingConnections.add(conn);
    }

    @Override
    public synchronized void removeConnection(Connection conn) {
        pendingConnections.remove(conn);
        clientConnections.remove(conn);
        serverConnections.remove(conn);
    }

    @Override
    public synchronized void closeAll() {
        for (Connection connection: serverConnections) {
            connection.close();
        }
        for (Connection connection: pendingConnections) {
            connection.close();
        }
        for (Connection connection: clientConnections) {
            connection.close();
        }
        pendingConnections.clear();
        serverConnections.clear();
        clientConnections.clear();
    }

    @Override
    public synchronized int getLoad() {
        return clientConnections.size();
    }
}
