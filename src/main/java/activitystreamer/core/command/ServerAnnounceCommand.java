package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

import java.net.InetAddress;

public class ServerAnnounceCommand implements ICommand {
    private final String command = "SERVER_ANNOUNCE";

    @JsonRequired
    private String id;
    @JsonRequired
    private int load;
    @JsonRequired
    private InetAddress hostname;
    @JsonRequired
    private int port;

    public ServerAnnounceCommand(String id, int load, InetAddress hostname, int port) {
        this.id = id;
        this.load = load;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public String filter() {
        if (id == null) {
            return "Server announce command should contain an id field";
        }
        if (hostname == null) {
            return "Server announce command should contain a hostname field";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServerAnnounceCommand &&
            id.equals(((ServerAnnounceCommand) obj).getId()) &&
            hostname.equals(((ServerAnnounceCommand) obj).getHostname()) &&
            load == (((ServerAnnounceCommand) obj).getLoad()) &&
            port == (((ServerAnnounceCommand) obj).getPort());
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
}
