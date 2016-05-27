package activitystreamer.server.services.contracts;

import activitystreamer.core.shared.Connection;

import java.net.InetAddress;
import java.util.Set;

public interface RemoteServerStateService {
    void updateState(String id, int load, InetAddress hostname, int port,
                     int secureLoad, InetAddress secureHostname, int securePort, Connection connection);
    void announce();
    boolean loadBalance(Connection connection);
}
