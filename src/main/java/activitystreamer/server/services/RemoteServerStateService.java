
package activitystreamer.server.services;

import activitystreamer.server.Control;
import activitystreamer.server.ServerState;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

public class RemoteServerStateService {
    private static final int TIME_LAPSE = 10;

    private final Control control;
    private HashMap<String, ServerState> states;
    private HashMap<String, Long>        statesLastTime;

    public RemoteServerStateService(Control control) {
        this.states = new HashMap<String, ServerState>();
        this.statesLastTime = new HashMap<String, Long>();
        this.control = control;
    }

    public synchronized void updateState(String id, ServerState state) {
        states.put(id, state);
        statesLastTime.put(id, System.currentTimeMillis() / 1000L);
    }

    public List<String> getKnownServerIds() {
        List<String> serverIds = new ArrayList<String>();
        for (Map.Entry<String, ServerState> s : this.states.entrySet()) {
            serverIds.add(s.getKey());
        }

        return serverIds;
    }

    public ServerState getServerToRedirectTo() {
        synchronized (control) {
            int threshold = control.getLoad() - 2;
            for (ServerState server: states.values()) {
                if (server.getLoad() <= threshold) {
                    return server;
                }
            }
            return null;
        }
    }

    public void printDebugState() {
        for (Map.Entry<String, ServerState> s : this.states.entrySet()) {
            String id = s.getKey();
            ServerState state = s.getValue();
            String tm = "ACTIVE";
            if ((System.currentTimeMillis() / 1000L) - statesLastTime.get(id) > TIME_LAPSE) {
                tm = "INACTIVE";
            }
            System.out.printf(id + " : " + state.getHostname().toString() + " : " + state.getPort() + " : " + state.getLoad() + " : " + tm + "\n");
        }
    }
}
