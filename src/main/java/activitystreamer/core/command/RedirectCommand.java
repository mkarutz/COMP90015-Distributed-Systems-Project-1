package activitystreamer.core.command;

import java.net.InetAddress;

public class RedirectCommand {
    private final String command = "REDIRECT";
    private InetAddress hostname;
    private int port;

    public String getCommand() {
        return command;
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
