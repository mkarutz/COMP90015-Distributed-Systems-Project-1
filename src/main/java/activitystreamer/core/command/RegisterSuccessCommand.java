package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class RegisterSuccessCommand implements Command {
    private final String command = "REGISTER_SUCCESS";
    @JsonRequired
    private String info;
    private String username;
    private String secret;

    public RegisterSuccessCommand(String info) {
        this.info = info;
    }

    public RegisterSuccessCommand(String info, String username, String secret) {
        this.info = info;
        this.username = username;
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterSuccessCommand that = (RegisterSuccessCommand) o;

        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;
        return secret != null ? secret.equals(that.secret) : that.secret == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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
