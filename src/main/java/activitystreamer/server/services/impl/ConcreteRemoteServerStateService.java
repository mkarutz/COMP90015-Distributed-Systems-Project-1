
package activitystreamer.server.services.impl;

import activitystreamer.core.command.RedirectCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.util.Settings;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcreteRemoteServerStateService implements RemoteServerStateService {
    Logger log = LogManager.getLogger();
    private HashMap<String, ServerState> states;

    private final ConnectionManager connectionManager;

    @Inject
    public ConcreteRemoteServerStateService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.states = new HashMap<>();
    }

    @Override
    public synchronized void updateState(String id, int load, InetAddress hostname, int port, int securePort) {
        states.put(id, new ServerState(hostname, port, load, securePort));
    }

    @Override
    public synchronized Set<String> getKnownServerIds() {
        Set<String> serverIds = new HashSet<>();
        for (Map.Entry<String, ServerState> s: this.states.entrySet()) {
            serverIds.add(s.getKey());
        }

        return serverIds;
    }

    @Override
    public synchronized void announce() {
        connectionManager.eachServerConnection(new Announcer(connectionManager.getLoad()));
    }

    @Override
    public synchronized void loadBalance(Connection connection) {
        ServerState redirectTo;
        if (connection.getSocket().getLocalPort() == Settings.getSecureLocalPort()) {
            if ((redirectTo = getSecureServerToRedirectTo()) != null) {
                log.debug("Redirecting Securely!!!!!!!!!!!!!!!");
                connection.pushCommand(new RedirectCommand(redirectTo.getHostname(), redirectTo.getSecurePort()));
                connectionManager.closeConnection(connection);
            }
        } else {
            if ((redirectTo = getInsecureServerToRedirectTo()) != null) {
                log.debug("Redirecting Insecurely!!!!!!!!!!!!!!!");
                connection.pushCommand(new RedirectCommand(redirectTo.getHostname(), redirectTo.getPort()));
                connectionManager.closeConnection(connection);
            }
        }
    }

    private ServerState getSecureServerToRedirectTo() {
        for (ServerState serverState: states.values()) {
            if (serverState.isSecure() && serverState.getLoad() < connectionManager.getLoad() - 1) {
                return serverState;
            }
        }
        return null;
    }

    private ServerState getInsecureServerToRedirectTo() {
        for (ServerState serverState: states.values()) {
            if (serverState.getLoad() < connectionManager.getLoad() - 1) {
                return serverState;
            }
        }
        return null;
    }

    private class Announcer implements ConnectionManager.ConnectionCallback {
        private int load;

        public Announcer(int load) {
            this.load = load;
        }

        public void execute(Connection connection) {
            connection.pushCommand(new ServerAnnounceCommand(
                    Settings.getId(),
                    load,
                    connection.getSocket().getLocalAddress(),
                    Settings.getLocalPort(),
                    Settings.getSecureLocalPort()
            ));
        }
    }
}
