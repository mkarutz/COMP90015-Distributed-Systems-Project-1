
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

public class ConcreteRemoteServerStateService implements RemoteServerStateService {
    private HashMap<String, ServerState> states;

    private final ConnectionManager connectionManager;

    @Inject
    public ConcreteRemoteServerStateService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.states = new HashMap<>();
    }

    @Override
    public synchronized void updateState(String id, int load, InetAddress hostname, int port) {
        states.put(id, new ServerState(hostname, port, load));
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
        if ((redirectTo = getServerToRedirectTo()) != null) {
            connection.pushCommand(new RedirectCommand(redirectTo.getHostname(), redirectTo.getPort()));
            connection.close();
        }
    }

    private ServerState getServerToRedirectTo() {
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
                    Settings.getLocalPort()
            ));
        }
    }
}
