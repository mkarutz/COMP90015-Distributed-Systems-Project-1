package activitystreamer.core.command;

public class LockRequestCommand implements ICommand {
    private final String command = "LOCK_REQUEST";
    private String username;
    private String secret;

    public LockRequestCommand(String username, String secret) {
        this.setUsername(username);
        this.setSecret(secret);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LockRequestCommand &&
            username.equals(((LockRequestCommand) obj).getUsername()) &&
            secret.equals(((LockRequestCommand) obj).getSecret());
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
}
