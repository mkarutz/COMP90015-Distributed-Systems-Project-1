package activitystreamer.server.services;

import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.ConnectionStateService;

public class ServerAuthService {

    private ConnectionStateService connState;

    public ServerAuthService(ConnectionStateService connState){
        this.connState=connState;
    }

    public boolean isAuthenticated(Connection conn) {
        if (connState.getConnectionType(conn) == ConnectionStateService.ConnectionType.SERVER){
            return true;
        }
        return false;
    }
}
