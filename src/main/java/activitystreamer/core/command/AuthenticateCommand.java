package activitystreamer.core.command;

public class AuthenticateCommand implements ICommand {
    private String secret;

    public AuthenticateCommand(String secret) {
        this.secret = secret;
    }

    @Override
    public String filter() {
        if (secret == null) {
            secret = "Authenticate command should contain a secret";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AuthenticateCommand && secret.equals(((AuthenticateCommand) obj).getSecret());
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
