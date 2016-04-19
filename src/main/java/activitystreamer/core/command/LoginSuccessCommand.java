package activitystreamer.core.command;

public class LoginSuccessCommand {
    private final String command = "LOGIN_SUCCESS";
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
