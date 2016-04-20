package activitystreamer.core.command;

public class RegisterCommand implements ICommand {
    private final String command = "REGISTER";
    private String username;
    private String secret;

    public RegisterCommand(String username, String secret) {
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
