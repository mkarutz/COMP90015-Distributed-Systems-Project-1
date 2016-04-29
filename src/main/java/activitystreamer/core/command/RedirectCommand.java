package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

import java.net.InetAddress;

public class RedirectCommand implements ICommand {
    private final String command = "REDIRECT";
    @JsonRequired
    private InetAddress hostname;
    @JsonRequired
    private int port;

    public RedirectCommand(InetAddress hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public String filter() {
        if (hostname == null) {
            return "Redirect command should contain a hostname";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RedirectCommand &&
            hostname.equals(((RedirectCommand) obj).getHostname()) &&
            port == (((RedirectCommand) obj).getPort());
    }

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
