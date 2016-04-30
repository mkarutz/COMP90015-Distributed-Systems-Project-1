package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class LockAllowedCommand implements Command {
    private final String command = "LOCK_ALLOWED";
    @JsonRequired
    private String username;
    @JsonRequired
    private String secret;
    @JsonRequired
    private String server;

    public LockAllowedCommand(String username, String secret, String server) {
        this.username = username;
        this.secret = secret;
        this.server = server;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LockAllowedCommand &&
            username.equals(((LockAllowedCommand) obj).getUsername()) &&
            secret.equals(((LockAllowedCommand) obj).getSecret()) &&
            server.equals(((LockAllowedCommand) obj).getServerId());
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServerId() {
        return server;
    }

    public void setServerId(String server) {
        this.server = server;
    }
}
