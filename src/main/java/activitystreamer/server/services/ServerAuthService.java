package activitystreamer.server.services;

import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;

public class ServerAuthService {

    private ConnectionStateService connectionStateService;

    public ServerAuthService(ConnectionStateService connectionStateService){
        this.connectionStateService = connectionStateService;
    }

    public boolean authenticate(Connection conn, String secret) {
        if (Settings.getSecret().equals(secret)) {
            connectionStateService.setConnectionType(conn, ConnectionStateService.ConnectionType.SERVER);
            return true;
        }
        return false;
    }

    public boolean isAuthenticated(Connection conn) {
        return connectionStateService.getConnectionType(conn) == ConnectionStateService.ConnectionType.SERVER;
    }
}
