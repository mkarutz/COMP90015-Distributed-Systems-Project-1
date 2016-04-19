package activitystreamer.core.command;

import java.net.InetAddress;

public class ServerAnnounceCommand {
    private final String command = "SERVER_ANNOUNCE";
    private String id;
    private int load;
    private InetAddress hostname;
    private int port;
}
