package activitystreamer.core.command;

public class AuthenticateCommand {
    private final String command = "AUTHENTICATE";
    private String secret;

    public String getCommand() {
        return command;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
