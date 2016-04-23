package activitystreamer.core.command;

public class LockDeniedCommand implements ICommand {
    private final String command = "LOCK_DENIED";
    private String username;
    private String secret;

    public LockDeniedCommand(String username, String secret) {
        this.username = username;
        this.secret = secret;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LockDeniedCommand &&
            username.equals(((LockDeniedCommand) obj).getUsername()) &&
            secret.equals(((LockDeniedCommand) obj).getSecret());
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
