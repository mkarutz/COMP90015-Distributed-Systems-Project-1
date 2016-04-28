package activitystreamer.core.command;

public class LockAllowedCommand implements ICommand {
    private final String command = "LOCK_ALLOWED";
    private String username;
    private String secret;
    private String server;

    public LockAllowedCommand(String username, String secret, String server) {
        this.username = username;
        this.secret = secret;
        this.server = server;
    }

    @Override
    public String filter() {
        if (username == null) {
            return "Lock allowed command should contain a username";
        }
        if (secret == null) {
            return "Lock allowed command should contain a secret";
        }
        if (server == null) {
            return "Lock allowed command should contain a server id";
        }
        return null;
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
