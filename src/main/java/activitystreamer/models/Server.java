package activitystreamer.models;

import java.net.InetAddress;

public class Server {
    private final String id;
    private final String secret;
    private int load;
    private InetAddress hostname;
    private int port;

    public Server(String id, String secret) throws Exception {
        if (id == null || secret == null) {
            throw new Exception("Server id and secret must not be null.");
        }
        this.id = id;
        this.secret = secret;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Server && id.equals(((Server) o).id);
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
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
}
