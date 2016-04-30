package activitystreamer.server.services.impl;

import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.util.Settings;
import com.google.inject.Inject;

import java.util.HashSet;
import java.util.Set;

public class ConcreteServerAuthService implements activitystreamer.server.services.contracts.ServerAuthService {
    private ConnectionManager connectionManager;

    private Set<Connection> authenticatedServers = new HashSet<>();

    @Inject
    public ConcreteServerAuthService(ConnectionManager connectionManager){
        this.connectionManager = connectionManager;
    }

    @Override
    public synchronized void logout(Connection conn) {
        authenticatedServers.remove(conn);
        connectionManager.removeConnection(conn);
    }

    @Override
    public synchronized boolean authenticate(Connection conn, String secret) {
        if (Settings.getSecret().equals(secret)) {
            authenticatedServers.add(conn);
            connectionManager.addServerConnection(conn);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean isAuthenticated(Connection conn) {
        return authenticatedServers.contains(conn);
    }
}
