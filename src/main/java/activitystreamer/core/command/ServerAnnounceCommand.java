package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

import java.net.InetAddress;

public class ServerAnnounceCommand implements Command {
    private final String command = "SERVER_ANNOUNCE";

    @JsonRequired
    private String id;
    @JsonRequired
    private int load;
    @JsonRequired
    private InetAddress hostname;
    @JsonRequired
    private int port;

    private int securePort = -1;

    public ServerAnnounceCommand() {
    }

    public ServerAnnounceCommand(String id, int load, InetAddress hostname, int port, int securePort) {
        this.id = id;
        this.load = load;
        this.hostname = hostname;
        this.port = port;
        this.securePort = securePort;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServerAnnounceCommand &&
            id.equals(((ServerAnnounceCommand) obj).getId()) &&
            hostname.equals(((ServerAnnounceCommand) obj).getHostname()) &&
            load == (((ServerAnnounceCommand) obj).getLoad()) &&
            port == (((ServerAnnounceCommand) obj).getPort()) &&
            securePort == (((ServerAnnounceCommand) obj).getSecurePort());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getSecurePort() {
        return securePort;
    }

    public void setSecurePort(int securePort) {
        this.securePort = securePort;
    }
}
