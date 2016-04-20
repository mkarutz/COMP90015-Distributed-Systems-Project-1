package activitystreamer.core.command;

public class AuthenticationFailCommand implements ICommand {
    private final String command = "AUTHENTICATION_FAIL";
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
