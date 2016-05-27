package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

import java.net.InetAddress;

public class ServerAnnounceCommand implements Command {
    private final String command = "SERVER_ANNOUNCE";

    @JsonRequired
    private String id;

    private int insecureid;
    @JsonRequired
    private int load;
    @JsonRequired
    private InetAddress hostname;
    @JsonRequired
    private int port;

    private int secureid;
    private int secureLoad;

    private InetAddress secureHostname;

    private int securePort = -1;

    public ServerAnnounceCommand() {
    }

    public ServerAnnounceCommand(String id, int load, InetAddress hostname, int port) {
        this.id = id;
        this.load = load;
        this.hostname = hostname;
        this.port = port;
    }

    public ServerAnnounceCommand(String id, int load, InetAddress hostname, int port, int secureLoad, InetAddress secureHostname, int securePort) {
        this.id = id;
        this.load = load;
        this.hostname = hostname;
        this.port = port;
        this.secureLoad = secureLoad;
        this.secureHostname = secureHostname;
        this.securePort = securePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerAnnounceCommand that = (ServerAnnounceCommand) o;

        if (load != that.load) return false;
        if (port != that.port) return false;
        if (secureLoad != that.secureLoad) return false;
        if (securePort != that.securePort) return false;
        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null)
            return false;
        return secureHostname != null ? secureHostname.equals(that.secureHostname) : that.secureHostname == null;
    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + load;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + secureLoad;
        result = 31 * result + (secureHostname != null ? secureHostname.hashCode() : 0);
        result = 31 * result + securePort;
        return result;
    }

    public String getCommand() {
        return command;
    }

    public String getId() {
        return id;
    }

    public int getLoad() {
        return load;
    }

    public InetAddress getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getSecureLoad() {
        return secureLoad;
    }

    public InetAddress getSecureHostname() {
        return secureHostname;
    }

    public int getSecurePort() {
        return securePort;
    }
}