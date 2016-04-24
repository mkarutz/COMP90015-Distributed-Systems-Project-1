
package activitystreamer.server.services;

import activitystreamer.server.ServerState;
import java.util.Map;
import java.util.HashMap;
import java.net.InetAddress;

public class RemoteServerStateService {

    private HashMap<String, ServerState> states;

    public RemoteServerStateService() {
        this.states = new HashMap<String, ServerState>();
    }

    public synchronized void updateState(String id, ServerState state) {
        states.put(id, state);
    }

    public synchronized boolean removeStateByHostAndPort(InetAddress host, int port) {
        String remId = null;
        for (Map.Entry<String, ServerState> s : this.states.entrySet()) {
            String id = s.getKey();
            ServerState state = s.getValue();
            if (state.getHostname().getHostAddress().equals(host.getHostAddress()) && state.getPort() == port) {
                remId = id;
            }
        }
        if (remId != null) {
            states.remove(remId);
            return true;
        }
        return false;
    }

    public void printDebugState() {
        for (Map.Entry<String, ServerState> s : this.states.entrySet()) {
            String id = s.getKey();
            ServerState state = s.getValue();
            System.out.printf(id + " : " + state.getHostname().toString() + " : " + state.getPort() + " : " + state.getLoad() + "\n");
        }
    }

}
