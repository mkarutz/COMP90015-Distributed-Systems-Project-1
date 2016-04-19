package activitystreamer.core.command;

public class LoginCommand {
    private final String command = "LOGIN";
    private String username;
    private String secret;

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
