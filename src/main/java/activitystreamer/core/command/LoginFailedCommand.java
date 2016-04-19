package activitystreamer.core.command;

public class LoginFailedCommand {
    private final String command = "LOGIN_FAILED";
    private String info;

    public String getCommand() {
        return command;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
