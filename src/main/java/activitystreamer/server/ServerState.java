package activitystreamer.server;

import java.net.InetAddress;

public class ServerState {
    private int load;
    private InetAddress hostname;
    private int port;

    public ServerState(InetAddress hostname, int port, int load) {
        this.hostname = hostname;
        this.port = port;
        this.load = load;
    }

    public InetAddress getHostname() {
        return hostname;
    }

    public void setHostname(InetAddress hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }
}
