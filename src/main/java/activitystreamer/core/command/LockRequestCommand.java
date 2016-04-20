package activitystreamer.core.command;

public class LockRequestCommand implements ICommand {
    private final String command = "LOCK_REQUEST";
    private String username;
    private String secret;

    public LockRequestCommand(String username, String secret) {
        this.setUsername(username);
        this.setSecret(secret);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return this.username;
    }

    public String getSecret() {
        return this.secret;
    }
}
